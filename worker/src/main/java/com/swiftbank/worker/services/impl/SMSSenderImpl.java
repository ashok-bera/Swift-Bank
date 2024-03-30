package com.swiftbank.worker.services.impl;

import com.swiftbank.worker.configs.TwilioConfig;
import com.swiftbank.worker.dtos.EventMessageDTO;
import com.swiftbank.worker.services.NotificationSender;
import com.twilio.Twilio;
import com.twilio.exception.ApiException;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

@Slf4j
@Service(value = "SMSSenderImpl")
@PropertySource(value = "classpath:application.yml")
public class SMSSenderImpl implements NotificationSender {

    @Value("${twilio.from-number}")
    private String fromPhoneNumber;

    @Override
    public void send(EventMessageDTO eventMessageDTO) {
        log.info("Sending SMS to :: {}, message :: {}", eventMessageDTO.getMobile(), eventMessageDTO.getBody());
        try {
            Message message = Message.creator(
                    new PhoneNumber(eventMessageDTO.getMobile()),
                    new PhoneNumber(fromPhoneNumber),
                    eventMessageDTO.getBody()
            ).create();
            log.info("Message Sent, ID :: {}", message.getSid());
        } catch (final ApiException e) {
            throw new RuntimeException("Error while sending SMS :: {}" + e.getMessage());
        }
    }
}
