package com.swiftbank.worker.services.impl;

import com.swiftbank.worker.dtos.EventMessageDTO;
import com.swiftbank.worker.services.NotificationSender;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Objects;

@Slf4j
@Service(value = "MailSenderImpl")
@PropertySource(value = "classpath:application.yml")
public class MailSenderImpl implements NotificationSender {

    @Qualifier(value = "JavaMailSender")
    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;


    public MailSenderImpl(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Override
    public void send(EventMessageDTO eventMessageDTO) {
        if (Objects.isNull(eventMessageDTO.getFile())) {
            sendMail(eventMessageDTO.getEmail(), eventMessageDTO.getSubject(), eventMessageDTO.getBody());
        } else {
            sendMailWithAttachment(eventMessageDTO.getEmail(), eventMessageDTO.getSubject(),
                    eventMessageDTO.getBody(), eventMessageDTO.getFileName(), eventMessageDTO.getFile());
        }
    }
    private void sendMail(String toEmail, String subject, String messageBody) {
        SimpleMailMessage mail = new SimpleMailMessage();
        try {
            mail.setFrom(fromEmail);
            mail.setTo(toEmail);
            mail.setSubject(subject);
            mail.setText(messageBody);
            log.info("Sending Mail to :: {}, message :: {}", toEmail, messageBody);
            javaMailSender.send(mail);
        } catch (Exception e) {
            throw new RuntimeException("Error while sending Mail :: {}" + e.getMessage());
        }
    }
    private void sendMailWithAttachment(String toEmail, String subject, String messageBody, String fileName, File attachment) {
        MimeMessage message = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, Boolean.TRUE);
            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(messageBody);
            FileSystemResource file
                    = new FileSystemResource(attachment);
            helper.addAttachment(fileName, file);
            log.info("Sending Mail to :: {}, message :: {}", toEmail, messageBody);
            javaMailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Error while sending Mail :: {}" + e.getMessage());
        }
    }
}
