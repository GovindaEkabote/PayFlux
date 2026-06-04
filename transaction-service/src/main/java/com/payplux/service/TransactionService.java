package com.payplux.service;


import com.payplux.dto.TransferRequest;
import com.payplux.model.Transaction;

import java.util.List;

public interface TransactionService {
    Transaction createTransaction(Transaction transaction);
    List<Transaction>  getAllTransactions();
    Transaction transfer(TransferRequest request);
    Transaction getTransactionByReferenceId(String referenceId);

}
