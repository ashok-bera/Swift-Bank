package com.bera.swiftbank.service.impl;

import com.bera.swiftbank.config.AppConstants;
import com.bera.swiftbank.dtos.*;
import com.bera.swiftbank.entity.User;
import com.bera.swiftbank.enums.NotificationType;
import com.bera.swiftbank.repository.UserRepo;
import com.bera.swiftbank.service.EmailService;
import com.bera.swiftbank.service.TransactionService;
import com.bera.swiftbank.service.UserService;
import com.bera.swiftbank.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.springframework.kafka.support.KafkaHeaders.TOPIC;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserRepo userRepo;
    @Autowired
    TransactionService transactionService;
    @Autowired
    KafkaTemplate<String, EventMessageDTO> kafkaTemplate;

//    @Autowired
//    EmailService emailService;

    @Override
    public BankResponse createAccount(UserRequest userRequest) {
        if(userRepo.existsByEmail(userRequest.getEmail())){
                return BankResponse.builder()
                        .responseCode(AppConstants.ACCOUNT_EXISTS_CODE)
                        .respMsg(AppConstants.ACCOUNT_EXISTS_MSG)
                        .accountInfo(null)
                        .build();
       }

        User newuser = User.builder()
                .firtsName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .gender(userRequest.getGender())
                .address(userRequest.getAddress())
                .accountNo(AccountUtils.generateAccountNo())
                .email(userRequest.getEmail())
                .balance(BigDecimal.ZERO)
                .phoneNumber(userRequest.getPhoneNUmber())
                .status("ACTIVE")
                .build();
        User savedUser = userRepo.save(newuser);
        // send mail from here
        // EmailDetails emailDetails = EmailDetails.builder().build();
        EventMessageDTO mailMessageDTO = new EventMessageDTO(
                NotificationType.MAIL,null,newuser.getEmail(),
                AppConstants.ACCOUNT_CREATION_EMAIL_SUBJECT,
                AppConstants.ACCOUNT_CREATION_EMAIL_BODY,
                null,null);

        EventMessageDTO smsMessageDTO = new EventMessageDTO(
                NotificationType.SMS,"+91" + newuser.getPhoneNumber(),null,
                null,
                AppConstants.ACCOUNT_CREATION_EMAIL_BODY,
                null,null);

        kafkaTemplate.send(AppConstants.TOPIC,mailMessageDTO);
        kafkaTemplate.send(AppConstants.TOPIC,smsMessageDTO);
        return BankResponse.builder()
                .responseCode(AppConstants.ACCOUNT_CREATION_CODE)
                .respMsg(AppConstants.ACCOUNT_CREATION_MSG)
                .accountInfo(AccountInfo.builder()
                        .accountName(savedUser.getFirtsName())
                        .accountBalance(savedUser.getBalance())
                        .accountNo(savedUser.getAccountNo())
                        .build())
                .build();

    }

    @Override
    public BankResponse balanceEnquiry(EnquiryRequest enquiryRequest) {
        Boolean isAccountExist = userRepo.existsByAccountNo(enquiryRequest.getAccountNo());
        if(!isAccountExist){
            return BankResponse.builder()
                    .responseCode(AppConstants.ACCOUNT_NOT_EXISTS_CODE)
                    .respMsg(AppConstants.ACCOUNT_NOT_EXISTS_MSG)
                    .accountInfo(null)
                    .build();
        }
        User user = userRepo.findByAccountNo(enquiryRequest.getAccountNo());
        return BankResponse.builder()
                .responseCode(AppConstants.ACCOUNT_EXISTS_CODE)
                .respMsg(AppConstants.ACCOUNT_EXISTS_MSG)
                .accountInfo(AccountInfo.builder()
                        .accountName(user.getFirtsName())
                        .accountBalance(user.getBalance())
                        .accountNo(user.getAccountNo())
                        .build())
                .build();
    }

    @Override
    public String nameEnquiry(EnquiryRequest enquiryRequest) {
        Boolean isAccountExist = userRepo.existsByAccountNo(enquiryRequest.getAccountNo());
        if(!isAccountExist){
            return AppConstants.ACCOUNT_NOT_EXISTS_MSG;
        }
        User user = userRepo.findByAccountNo(enquiryRequest.getAccountNo());
        return user.getFirtsName();
    }

    @Override
    public BankResponse debitAmount(CreditDebitRequest creditDebitRequest) {
        Boolean isAccountExist = userRepo.existsByAccountNo(creditDebitRequest.getAccountNo());
        if(!isAccountExist){
            return BankResponse.builder()
                    .responseCode(AppConstants.ACCOUNT_NOT_EXISTS_CODE)
                    .respMsg(AppConstants.ACCOUNT_NOT_EXISTS_MSG)
                    .accountInfo(null)
                    .build();
        }
        User debituser = userRepo.findByAccountNo(creditDebitRequest.getAccountNo());
        BigInteger availBal = debituser.getBalance().toBigInteger();
        BigInteger amount = creditDebitRequest.getAmount().toBigInteger();
        if(availBal.intValue()<amount.intValue()){
            return BankResponse.builder()
                    .responseCode(AppConstants.INSUFICIENT_BALANCE_CODE)
                    .respMsg(AppConstants.INSUFFICEIINSUFICIENT_BALANCE_MSG)
                    .accountInfo(null)
                    .build();
        }
        debituser.setBalance(debituser.getBalance().subtract(creditDebitRequest.getAmount()));
        userRepo.save(debituser);

        //send email from here

        EventMessageDTO mailMessageDTO = new EventMessageDTO(
                NotificationType.MAIL,null,debituser.getEmail(),
                AppConstants.ACCOUNT_DEBIT_EMAIL_MSG,
                AppConstants.ACCOUNT_DEBIT_EMAIL_BODY +" :" +creditDebitRequest.getAmount(),
                null,null);

        EventMessageDTO smsMessageDTO = new EventMessageDTO(
                NotificationType.SMS,"+91" + debituser.getPhoneNumber(),null,
                null,
                AppConstants.ACCOUNT_DEBIT_EMAIL_BODY +" :" +creditDebitRequest.getAmount(),
                null,null);

        kafkaTemplate.send(AppConstants.TOPIC,mailMessageDTO);
        kafkaTemplate.send(AppConstants.TOPIC,smsMessageDTO);

        TransactionDto transactionDto = TransactionDto.builder()
                .accountNo(debituser.getAccountNo())
                .transationType("DEBIT")
                .amount(creditDebitRequest.getAccountNo())
                .status("SUCCESS")
                .build();

        transactionService.saveTransaction(transactionDto);

        return BankResponse.builder()
                .responseCode(AppConstants.AMOUNT_DEBIT_SUCCESS_CODE)
                .respMsg(AppConstants.AMOUNT_DEBIT_SUCCESS_MSG)
                .accountInfo(AccountInfo.builder()
                        .accountName(debituser.getFirtsName())
                        .accountBalance(debituser.getBalance())
                        .accountNo(debituser.getAccountNo())
                        .build())
                .build();
    }

    @Override
    public BankResponse creditAmount(CreditDebitRequest creditDebitRequest) {
        Boolean isAccountExist = userRepo.existsByAccountNo(creditDebitRequest.getAccountNo());
        if(!isAccountExist){
            return BankResponse.builder()
                    .responseCode(AppConstants.ACCOUNT_NOT_EXISTS_CODE)
                    .respMsg(AppConstants.ACCOUNT_NOT_EXISTS_MSG)
                    .accountInfo(null)
                    .build();
        }
        User creditUser = userRepo.findByAccountNo(creditDebitRequest.getAccountNo());
        creditUser.setBalance(creditUser.getBalance().add(creditDebitRequest.getAmount()));
        userRepo.save(creditUser);
        //send email from here

        EventMessageDTO mailMessageDTO = new EventMessageDTO(
                NotificationType.MAIL,null,creditUser.getEmail(),
                AppConstants.ACCOUNT_CREDIT_EMAIL_MSG,
                AppConstants.ACCOUNT_CREDIT_EMAIL_BODY +" :" +creditDebitRequest.getAmount(),
                null,null);

        EventMessageDTO smsMessageDTO = new EventMessageDTO(
                NotificationType.SMS,"+91" + creditUser.getPhoneNumber(),null,
                null,
                AppConstants.ACCOUNT_CREDIT_EMAIL_BODY +" :" +creditDebitRequest.getAmount(),
                null,null);

        kafkaTemplate.send(AppConstants.TOPIC,mailMessageDTO);
        kafkaTemplate.send(AppConstants.TOPIC,smsMessageDTO);

        TransactionDto transactionDto = TransactionDto.builder()
                .accountNo(creditUser.getAccountNo())
                .transationType("CREDIT")
                .amount(creditDebitRequest.getAccountNo())
                .status("SUCCESS")
                .build();

        transactionService.saveTransaction(transactionDto);

        return BankResponse.builder()
                .responseCode(AppConstants.AMOUNT_CREDIT_SUCCESS_CODE)
                .respMsg(AppConstants.AMOUNT_CREDIT_SUCCESS_MSG)
                .accountInfo(AccountInfo.builder()
                        .accountName(creditUser.getFirtsName())
                        .accountBalance(creditUser.getBalance())
                        .accountNo(creditUser.getAccountNo())
                        .build())
                .build();
    }

    @Override
    public BankResponse transferAmount(TransferRequest transferRequest) {
        Boolean isSrcAccountExist = userRepo.existsByAccountNo(transferRequest.getSrcAccNo());
        Boolean isDestAccountExist = userRepo.existsByAccountNo(transferRequest.getDestAccNo());

        if(!isSrcAccountExist){
            return BankResponse.builder()
                    .responseCode(AppConstants.ACCOUNT_NOT_EXISTS_CODE)
                    .respMsg(AppConstants.ACCOUNT_NOT_EXISTS_MSG)
                    .accountInfo(null)
                    .build();
        }
        if(!isDestAccountExist){
            return BankResponse.builder()
                    .responseCode(AppConstants.ACCOUNT_NOT_EXISTS_CODE)
                    .respMsg(AppConstants.ACCOUNT_NOT_EXISTS_MSG)
                    .accountInfo(null)
                    .build();
        }
        User srcUser = userRepo.findByAccountNo(transferRequest.getSrcAccNo());
        BigInteger availBal = srcUser.getBalance().toBigInteger();
        BigInteger amount = transferRequest.getAmount().toBigInteger();
        if(availBal.intValue()<amount.intValue()){
            return BankResponse.builder()
                    .responseCode(AppConstants.INSUFICIENT_BALANCE_CODE)
                    .respMsg(AppConstants.INSUFFICEIINSUFICIENT_BALANCE_MSG)
                    .accountInfo(null)
                    .build();
        }
        User destuser = userRepo.findByAccountNo(transferRequest.getDestAccNo());
        destuser.setBalance(destuser.getBalance().add(transferRequest.getAmount()));
        userRepo.save(destuser);
        srcUser.setBalance(srcUser.getBalance().subtract(transferRequest.getAmount()));
        userRepo.save(srcUser);
        // send 2 mails from here

        EventMessageDTO mailMessageDTO = new EventMessageDTO(
                NotificationType.MAIL,null,srcUser.getEmail(),
                AppConstants.ACCOUNT_DEBIT_EMAIL_MSG,
                AppConstants.ACCOUNT_DEBIT_EMAIL_BODY +" :" +transferRequest.getAmount(),
                null,null);

        EventMessageDTO mailMessageDTO1 = new EventMessageDTO(
                NotificationType.MAIL,null,destuser.getEmail(),
                AppConstants.ACCOUNT_CREDIT_EMAIL_MSG,
                AppConstants.ACCOUNT_CREDIT_EMAIL_BODY +" :" +transferRequest.getAmount(),
                null,null);



        kafkaTemplate.send(AppConstants.TOPIC,mailMessageDTO);
        kafkaTemplate.send(AppConstants.TOPIC,mailMessageDTO1);

        TransactionDto transactionDto = TransactionDto.builder()
                .accountNo(destuser.getAccountNo())
                .transationType("CREDIT")
                .amount(String.valueOf(transferRequest.getAmount().toBigInteger()))
                .status("SUCCESS")
                .build();
        transactionService.saveTransaction(transactionDto);

        TransactionDto transactionDto1 = TransactionDto.builder()
                .accountNo(srcUser.getAccountNo())
                .transationType("DEBIT")
                .amount(String.valueOf(transferRequest.getAmount()))
                .status("SUCCESS")
                .build();
        transactionService.saveTransaction(transactionDto1);

        return BankResponse.builder()
                .responseCode(AppConstants.AMOUNT_TRANSFER_SUCCESS_CODE)
                .respMsg(AppConstants.AMOUNT_TRANSFER_SUCCESS_MSG)
                .accountInfo(AccountInfo.builder()
                        .accountName(srcUser.getFirtsName())
                        .accountBalance(srcUser.getBalance())
                        .accountNo(transferRequest.getSrcAccNo())
                        .build())
                .build();
    }

}
