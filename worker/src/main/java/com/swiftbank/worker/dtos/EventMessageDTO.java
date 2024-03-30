package com.swiftbank.worker.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.swiftbank.worker.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;
import org.springframework.core.io.FileSystemResource;

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
