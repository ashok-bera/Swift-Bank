package com.bera.swiftbank.controller;

import com.bera.swiftbank.entity.Transaction;
import com.bera.swiftbank.service.BankStatement;
import com.itextpdf.text.DocumentException;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileNotFoundException;
import java.util.List;

@RestController
@RequestMapping("/bankStatement")
@AllArgsConstructor
public class TransactionController {
    @Autowired
    private BankStatement bankStatement;

    @GetMapping("/")
    public List<Transaction> genearetBankStatement(@RequestParam String AccountNo,
                                                   @RequestParam String startDate,
                                                   @RequestParam String endDate) throws DocumentException, FileNotFoundException {
        return bankStatement.generateStatement(AccountNo,startDate,endDate);
    }
}
