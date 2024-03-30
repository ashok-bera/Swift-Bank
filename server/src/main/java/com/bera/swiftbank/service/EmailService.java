package com.bera.swiftbank.service;

import com.bera.swiftbank.dtos.EmailDetails;

public interface EmailService {
    void sendEmailAlerts(EmailDetails emailDetails);
    void sendEmailWithAttachments(EmailDetails emailDetails);
}
