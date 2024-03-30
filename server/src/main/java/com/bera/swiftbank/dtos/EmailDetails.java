package com.bera.swiftbank.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailDetails {
    private  String recipeint;
    private String msgBody;
    private String subject;
    private String attachment;
}
