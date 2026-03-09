package com.bank.antifraud.service;

import com.bank.antifraud.enums.FraudReason;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FraudDecision {
    private final boolean isBlocked;
    private final boolean isSuspicious;
    private final FraudReason blockedReason ;
    private final FraudReason suspiciousReason;
}
