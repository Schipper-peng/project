package com.bank.antifraud.dto;

import com.bank.antifraud.enums.FraudReason;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SuspiciousAccountTransferDto {

    private Long accountTransferId;

    private Boolean isBlocked;

    private Boolean isSuspicious;

    private String blockedReason;

    private String suspiciousReason;

}
