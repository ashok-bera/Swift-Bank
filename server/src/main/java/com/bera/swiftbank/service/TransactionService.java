package com.bera.swiftbank.service;

import com.bera.swiftbank.dtos.TransactionDto;

public interface TransactionService {
    void saveTransaction(TransactionDto transactionDto);
}
