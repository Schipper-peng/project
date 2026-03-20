package com.bank.antifraud.dto.suspicious;


import com.bank.antifraud.enums.FraudReason;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SuspiciousAccountTransferDto {

    private Long id;

    private Long accountTransferId;

    private Boolean isBlocked;

    private Boolean isSuspicious;

    private FraudReason blockedReason;

    private FraudReason suspiciousReason;

}
