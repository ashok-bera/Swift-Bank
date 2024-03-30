package com.swiftbank.worker.controller;

import com.swiftbank.worker.dtos.EventMessageDTO;
import com.swiftbank.worker.services.NotificationSender;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test/v1")
public class TestController {

    private final NotificationSender smsSender, emailSender;

    public TestController(
            @Qualifier("SMSSenderImpl") NotificationSender smsSender,
            @Qualifier("MailSenderImpl") NotificationSender emailSender) {
        this.smsSender = smsSender;
        this.emailSender = emailSender;
    }

    @PostMapping("/mail")
    public ResponseEntity<String> sendMail(@RequestBody EventMessageDTO eventMessageDTO) {
        try {
            emailSender.send(eventMessageDTO);
            return new ResponseEntity<>("Mail send successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/sms")
    public ResponseEntity<String> sendSMS(@RequestBody EventMessageDTO eventMessageDTO) {
        try {
            smsSender.send(eventMessageDTO);
            return new ResponseEntity<>("SMS sent successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
