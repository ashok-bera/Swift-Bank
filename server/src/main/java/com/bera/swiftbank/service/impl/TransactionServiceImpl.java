package com.bera.swiftbank.service.impl;

import com.bera.swiftbank.dtos.TransactionDto;
import com.bera.swiftbank.entity.Transaction;
import com.bera.swiftbank.repository.TransactionRepo;
import com.bera.swiftbank.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransactionServiceImpl implements TransactionService {
    @Autowired
    TransactionRepo transactionRepo;
    @Override
    public void saveTransaction(TransactionDto transactionDto) {
        Transaction transaction = Transaction.builder()
                .transactionType(transactionDto.getTransationType())
                .amount(transactionDto.getAmount())
                .accountNo(transactionDto.getAccountNo())
                .status("SUCCESS")
                .build();
        transactionRepo.save(transaction);
    }
}
