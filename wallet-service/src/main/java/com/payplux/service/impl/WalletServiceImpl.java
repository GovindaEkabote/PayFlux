package com.payplux.service.impl;

import com.payplux.dto.*;
import com.payplux.exception.NotFoundException;
import com.payplux.model.*;
import com.payplux.repository.TransactionRepository;
import com.payplux.repository.WalletHoldRepository;
import com.payplux.repository.WalletRepository;
import com.payplux.service.WalletService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final WalletHoldRepository walletHoldRepository;
    private final TransactionRepository transactionRepository;

    public WalletServiceImpl(
            WalletRepository walletRepository,
            WalletHoldRepository walletHoldRepository,
            TransactionRepository transactionRepository) {
        this.walletRepository = walletRepository;
        this.walletHoldRepository = walletHoldRepository;
        this.transactionRepository = transactionRepository;

    }

    @Override
    @Transactional
    public HoldResponse releaseHold(String holdReference) {

        WalletHold hold = walletHoldRepository
                .findByHoldReference(holdReference)
                .orElseThrow(() ->
                        new RuntimeException("Hold not found"));

        if (hold.getStatus() != Status.ACTIVE) {
            throw new RuntimeException("Hold is not active");
        }

        Wallet wallet = hold.getWalletId();

        // Return held amount back to available balance
        wallet.setAvailableBalance(
                wallet.getAvailableBalance()
                        .add(hold.getAmount())
        );
        walletRepository.save(wallet);
        // Mark hold as released
        hold.setStatus(Status.RELEASED);
        walletHoldRepository.save(hold);
        return null;
    }

    @Override
    @Transactional
    public WalletResponse createWallet(CreateWalletRequest request) {
        Wallet wallet = new Wallet(request.getUserId(), request.getCurrency());
        Wallet saved = walletRepository.save(wallet);

        return new WalletResponse(
                saved.getId(),
                saved.getUserId(),
                saved.getCurrency(),
                saved.getBalance(),
                saved.getAvailableBalance());
    }

    @Override
    public WalletResponse getWallet(String userId) {
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Wallet not found for user:"));

        return new WalletResponse(
                wallet.getId(), wallet.getUserId(), wallet.getCurrency(), wallet.getBalance(), wallet.getAvailableBalance()
        );
    }

    @Override
    @Transactional
    public WalletResponse credit(CreditRequest request) {
        // Validate amount
        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Credit amount must be positive");
        }

        // Fetch wallet with pessimistic lock
        Wallet wallet = walletRepository
                .findByUserIdAndCurrency(
                        request.getUserId(),
                        request.getCurrency())
                .orElseThrow(() ->
                        new NotFoundException("Wallet not found for userId: " + request.getUserId()
                                + " and currency: " + request.getCurrency()));

        // Validate currency matches (redundant since we filtered by currency, but good for safety)
        if (!wallet.getCurrency().equals(request.getCurrency())) {
            throw new IllegalArgumentException("Currency mismatch");
        }

        // Update balances
        wallet.setBalance(wallet.getBalance().add(request.getAmount()));
        wallet.setAvailableBalance(wallet.getAvailableBalance().add(request.getAmount()));
        wallet.setUpdatedAt(LocalDateTime.now()); // You should add this field or handle in @PreUpdate

        Wallet saved = walletRepository.save(wallet);

        // Create transaction record
        Transaction transaction = new Transaction();
        transaction.setSenderId(String.valueOf(0L)); // 0 or null for system credit
        transaction.setReceiverId(String.valueOf(wallet.getId())); // Use wallet ID, not userId
        transaction.setAmount(request.getAmount());
        transaction.setType(TransactionType.CREDIT);
        transaction.setStatus(TransactionStatus.SUCCESS);
        transaction.setRemarks("Wallet credited");
        // prePersist will handle referenceId, createdAt, updatedAt

        transactionRepository.save(transaction);

        return new WalletResponse(
                saved.getId(),
                saved.getUserId(),
                saved.getCurrency(),
                saved.getBalance(),
                saved.getAvailableBalance()
        );
    }

    @Override
    @Transactional
    public WalletResponse debit(DebitRequest request) {
        // Validate amount
        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Debit amount must be positive");
        }

        Wallet wallet = walletRepository
                .findByUserIdAndCurrency(request.getUserId(), request.getCurrency())
                .orElseThrow(() -> new NotFoundException("Wallet not found"));

        // Check sufficient balance
        if (wallet.getAvailableBalance().compareTo(request.getAmount()) < 0) {
            throw new RuntimeException("Insufficient available balance");
        }

        // Update balances
        wallet.setBalance(wallet.getBalance().subtract(request.getAmount()));
        wallet.setAvailableBalance(wallet.getAvailableBalance().subtract(request.getAmount()));

        Wallet saved = walletRepository.save(wallet);

        // Create transaction record
        Transaction transaction = new Transaction();
        transaction.setSenderId(String.valueOf(wallet.getId()));
        transaction.setReceiverId(String.valueOf(0L)); // System or recipient ID
        transaction.setAmount(request.getAmount());
        transaction.setType(TransactionType.DEBIT);
        transaction.setStatus(TransactionStatus.SUCCESS);
        transaction.setRemarks("Wallet debited");

        transactionRepository.save(transaction);

        return new WalletResponse(
                saved.getId(),
                saved.getUserId(),
                saved.getCurrency(),
                saved.getBalance(),
                saved.getAvailableBalance()
        );
    }

   @Override
    @Transactional
    public HoldResponse placeHold(HoldRequest request) {
        // Validate amount
        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Hold amount must be positive");
        }

        // Get wallet with pessimistic lock
        Wallet wallet = walletRepository.findByUserIdAndCurrency(
                        request.getUserId(),
                        request.getCurrency())
                .orElseThrow(() -> new NotFoundException(
                        "Wallet not found for user: " + request.getUserId()
                ));

        // Check sufficient available balance
        if (wallet.getAvailableBalance().compareTo(request.getAmount()) < 0) {
            throw new NotFoundException(
                    "Not enough balance to hold. Available: " + wallet.getAvailableBalance()
                            + ", Requested hold: " + request.getAmount()
            );
        }

        // Reduce available balance (hold the amount)
        wallet.setAvailableBalance(wallet.getAvailableBalance().subtract(request.getAmount()));

        // Create hold record
        WalletHold hold = new WalletHold();
        hold.setWalletId(wallet);
        hold.setAmount(request.getAmount());
        hold.setHoldReference(UUID.randomUUID().toString());
        hold.setStatus(Status.ACTIVE);
        hold.setCreatedAt(LocalDateTime.now());
        hold.setExpiredAt(LocalDateTime.now().plusMinutes(30));

        walletRepository.save(wallet);
        WalletHold savedHold = walletHoldRepository.save(hold);

        return new HoldResponse(
                savedHold.getHoldReference(),
                savedHold.getAmount(),
                savedHold.getStatus().toString()
        );
    }

    @Override
    @Transactional
    public WalletResponse captureHold(CaptureRequest request) {
        // Get hold with pessimistic lock
        WalletHold hold = walletHoldRepository.findByHoldReference(request.setHoldReference())
                .orElseThrow(() -> new NotFoundException("Hold not found: " ));

        // Validate hold status
        if (hold.getStatus() != Status.ACTIVE) {
            throw new IllegalStateException("Hold is not active. Current status: " + hold.getStatus());
        }

        // Check if hold has expired
        if (hold.getExpiredAt() != null && hold.getExpiredAt().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Hold has expired");
        }

        Wallet wallet = hold.getWalletId();

        // Reduce the main balance (capture the amount)
        wallet.setBalance(wallet.getBalance().subtract(hold.getAmount()));
        // Available balance is already reduced during hold placement

        hold.setStatus(Status.CAPTURED);

        walletRepository.save(wallet);
        walletHoldRepository.save(hold);

        // Create transaction record for capture
        Transaction transaction = new Transaction();
        transaction.setSenderId(String.valueOf(wallet.getId()));
        transaction.setReceiverId("0L");
        transaction.setAmount(hold.getAmount());
        transaction.setType(TransactionType.DEBIT);
        transaction.setStatus(TransactionStatus.SUCCESS);
        transaction.setRemarks("Hold captured: " + request.setHoldReference());

        transactionRepository.save(transaction);

        return new WalletResponse(
                wallet.getId(),
                wallet.getUserId(),
                wallet.getCurrency(),
                wallet.getBalance(),
                wallet.getAvailableBalance()
        );
    }


}