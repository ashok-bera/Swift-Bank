package com.bera.swiftbank.controller;

import com.bera.swiftbank.dtos.*;
import com.bera.swiftbank.service.UserService;
import io.swagger.v3.oas.annotations.OpenAPI31;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@Tag(name = "User Account Management Apis")
public class UserController {
    @Autowired
    UserService userService;

    @PostMapping("/user")
    public BankResponse createUser(@RequestBody UserRequest userRequest){
        return userService.createAccount(userRequest);
    }

    @PostMapping("/balanceEnq")
    public BankResponse findBalance(@RequestBody EnquiryRequest enquiryRequest){
        return userService.balanceEnquiry(enquiryRequest);
    }

    @PostMapping("/nameEnq")
    public BankResponse findName(@RequestBody EnquiryRequest enquiryRequest){
        return userService.balanceEnquiry(enquiryRequest);
    }

    @PostMapping("/credit")
    public BankResponse creditBalance(@RequestBody CreditDebitRequest creditDebitRequest){
        return userService.creditAmount(creditDebitRequest);
    }

    @PostMapping("/debit")
    public BankResponse debitBalance(@RequestBody CreditDebitRequest creditDebitRequest){
        return userService.debitAmount(creditDebitRequest);
    }

    @PostMapping("/transfer")
    public BankResponse debitBalance(@RequestBody TransferRequest transferRequest){
        return userService.transferAmount(transferRequest);
    }

}
