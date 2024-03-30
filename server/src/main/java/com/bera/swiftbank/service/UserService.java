package com.bera.swiftbank.service;

import com.bera.swiftbank.dtos.*;

public interface UserService {
    BankResponse createAccount(UserRequest userRequest);
    BankResponse balanceEnquiry(EnquiryRequest enquiryRequest);
    String nameEnquiry(EnquiryRequest enquiryRequest);
    BankResponse debitAmount(CreditDebitRequest creditDebitRequest);
    BankResponse creditAmount(CreditDebitRequest creditDebitRequest);
    BankResponse transferAmount(TransferRequest transferRequest);
}
