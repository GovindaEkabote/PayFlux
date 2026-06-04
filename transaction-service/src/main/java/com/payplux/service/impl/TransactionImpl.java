package com.payplux.service.impl;

import com.payplux.dto.TransferRequest;
import com.payplux.kafka.KafkaEventProducer;
import com.payplux.model.Transaction;
import com.payplux.model.TransactionStatus;
import com.payplux.model.TransactionType;
import com.payplux.repository.TransactionRepository;
import com.payplux.service.TransactionService;
import lombok.AllArgsConstructor;
import lombok.Value;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.List;

@Service
@AllArgsConstructor
public class TransactionImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final ObjectMapper objectMapper;
    private final KafkaEventProducer kafkaEventProducer;


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

        validateTransfer(request);

        Transaction transaction = new Transaction();
        transaction.setSenderId(request.getSenderId());
        transaction.setReceiverId(request.getReceiverId());
        transaction.setAmount(request.getAmount());
        transaction.setType(TransactionType.TRANSFER);

        // PENDING
        transaction.setStatus(TransactionStatus.PENDING);

        transaction = transactionRepository.save(transaction);

        kafkaEventProducer.sendTransactionEvent(
                transaction.getReferenceId(),
                transaction
        );

        return transaction;
    }

    @Override
    public Transaction getTransactionByReferenceId(String referenceId) {
        return transactionRepository.findByReferenceId(referenceId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Transaction not found with referenceId: " + referenceId));
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
