//package com.bera.swiftbank.service.impl;
//
//import com.bera.swiftbank.dtos.EmailDetails;
//import com.bera.swiftbank.service.EmailService;
//import jakarta.mail.MessagingException;
//import jakarta.mail.internet.MimeMessage;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.core.io.FileSystemResource;
//import org.springframework.mail.SimpleMailMessage;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.mail.javamail.MimeMessageHelper;
//import org.springframework.stereotype.Service;
//
//import java.io.File;
//import java.util.Objects;
//
//@Service
//@Slf4j
//public class EmailServiceImpl implements EmailService {
//    @Autowired
//    private JavaMailSender javaMailSender;
//    @Value("${spring.mail.username")
//    private String sender;
//
//    @Override
//    public void sendEmailAlerts(EmailDetails emailDetails) {
//        try{
//            SimpleMailMessage mailMessage = new SimpleMailMessage();
//            mailMessage.setFrom(sender);
//            mailMessage.setTo(emailDetails.getRecipeint());
//            mailMessage.setText(emailDetails.getMsgBody());
//            mailMessage.setSubject(emailDetails.getSubject());
//            javaMailSender.send(mailMessage);
//        }
//        catch (Exception e){
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    public void sendEmailWithAttachments(EmailDetails emailDetails) {
//        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
//        MimeMessageHelper mimeMessageHelper;
//        try{
//            mimeMessageHelper = new MimeMessageHelper(mimeMessage,true);
//            mimeMessageHelper.setFrom(sender);
//            mimeMessageHelper.setTo(emailDetails.getRecipeint());
//            mimeMessageHelper.setText(emailDetails.getMsgBody());
//            mimeMessageHelper.setSubject(emailDetails.getSubject());
//            FileSystemResource file = new FileSystemResource(new File(emailDetails.getAttachment()));
//            mimeMessageHelper.addAttachment(Objects.requireNonNull(file.getFilename()), file);
//            javaMailSender.send(mimeMessage);
//
//        } catch (MessagingException e) {
//            throw new RuntimeException(e);
//        }
//    }
//}
