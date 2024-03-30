package com.swiftbank.worker.services;

import com.swiftbank.worker.dtos.EventMessageDTO;

public interface NotificationSender {
    void send(EventMessageDTO eventMessageDTO);
}
