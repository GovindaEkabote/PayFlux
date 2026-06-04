package com.payplux.controller;

import com.payplux.dto.TransferRequest;
import com.payplux.model.Transaction;
import com.payplux.service.TransactionService;
import lombok.AllArgsConstructor;
//import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transactions")
@AllArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/transfer")
    public ResponseEntity<Transaction> transfer( @RequestBody TransferRequest request) {

        Transaction transaction = transactionService.transfer(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(transaction);
    }

    // ✅ Get all transactions
    @GetMapping
    public ResponseEntity<List<Transaction>> getAllTransactions() {

        return ResponseEntity.ok(transactionService.getAllTransactions());
    }

    @GetMapping("/reference/{referenceId}")
    public  ResponseEntity<Transaction> getReferenceById(@PathVariable String referenceId){
        Transaction transaction = transactionService.getTransactionByReferenceId(referenceId);
        return ResponseEntity.ok(transaction);
    }

}
