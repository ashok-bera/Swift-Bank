package com.bera.swiftbank.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountInfo {
    @Schema(name = "User Name")
    private String accountName;
    @Schema(name = "User Account No")
    private String accountNo;
    @Schema(name = "User Account Balance")
    private BigDecimal accountBalance;
}
