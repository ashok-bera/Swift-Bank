package com.swiftbank.worker.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.swiftbank.worker.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.io.File;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class EventMessageDTO {
    private NotificationType type;
    private String mobile;
    private String email;
    private String subject;
    private String body;
    private String fileName;
    private File file;
}
