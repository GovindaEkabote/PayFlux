package com.payplux.service.impl;

import com.payplux.dto.TransferRequest;
import com.payplux.model.Transaction;
import com.payplux.model.TransactionStatus;
import com.payplux.repository.TransactionRepository;
import com.payplux.service.TransactionService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.List;

@Service
@AllArgsConstructor
public class TransactionImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final ObjectMapper objectMapper;

    @Override
    public Transaction createTransaction(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    @Override
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    @Override
    @Transactional
    public Transaction transfer(TransferRequest request) {

        // ✅ 1. Validate request
        validateTransfer(request);

        // ✅ 2. Create transaction
        Transaction transaction = new Transaction();
        transaction.setSenderId(request.getSenderId());
        transaction.setReceiverId(request.getReceiverId());
        transaction.setAmount(request.getAmount());

        // PrePersist will set:
        // referenceId, createdAt, status = PENDING

        transaction = transactionRepository.save(transaction);

        try {
            // 🔥 3. Simulate business logic (later: wallet service)

            // Example: assume success
            transaction.setStatus(TransactionStatus.SUCCESS);

        } catch (Exception e) {

            transaction.setStatus(TransactionStatus.FAILED);
        }

        return transactionRepository.save(transaction);
    }

    private void validateTransfer(TransferRequest request) {

        if (request.getSenderId().equals(request.getReceiverId())) {
            throw new RuntimeException("Sender and Receiver cannot be same");
        }

        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Amount must be greater than 0");
        }
    }
}
