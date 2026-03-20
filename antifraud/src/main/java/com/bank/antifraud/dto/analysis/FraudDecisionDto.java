package com.bank.antifraud.dto.analysis;

import com.bank.antifraud.enums.FraudReason;
import com.bank.antifraud.enums.TransferType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FraudDecisionDto {
    private Long transferId;
    private TransferType transferType;
    private boolean isBlocked;
    private boolean isSuspicious;
    private FraudReason blockedReason;
    private FraudReason suspiciousReason;

}
