package com.swiftbank.worker.listeners;

import com.swiftbank.worker.dtos.EventMessageDTO;
import com.swiftbank.worker.services.NotificationSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EventListener {

    private final NotificationSender smsSender, emailSender;

    public EventListener(
            @Qualifier("SMSSenderImpl") NotificationSender smsSender,
            @Qualifier("MailSenderImpl") NotificationSender emailSender) {
        this.smsSender = smsSender;
        this.emailSender = emailSender;
    }

    @KafkaListener(id = "notification-events-group",
            groupId = "notification-events-group",
            topics = "notification-messages",
            containerFactory = "eventsListenerContainerFactory")
    public void listenMessage(
            EventMessageDTO eventMessageDTO,
            Acknowledgment acknowledgment) {
        log.info("Notification Message :: {}", eventMessageDTO);

        try {
            switch (eventMessageDTO.getType()) {
                case SMS -> smsSender.send(eventMessageDTO);
                case MAIL -> emailSender.send(eventMessageDTO);
                default -> throw new RuntimeException("Invalid Message Type :: " + eventMessageDTO.getType());
            }
        } catch (Exception e) {
            log.error("Error while sending notification :: {}", e.getMessage());
        }

        acknowledgment.acknowledge();
    }
}
