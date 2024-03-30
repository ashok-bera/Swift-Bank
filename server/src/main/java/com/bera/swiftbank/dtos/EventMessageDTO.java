package com.bera.swiftbank.dtos;

import com.bera.swiftbank.enums.NotificationType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.io.File;

@Data
@ToString
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class EventMessageDTO {
    private final NotificationType type;
    private final String mobile;
    private final String email;
    private final String subject;
    private final String body;
    private final String fileName;
    private final File file;
}