package com.bank.antifraud.enums;

import lombok.Getter;

@Getter
public enum FraudReason {
    AMOUNT_OVER_BLOCK_THRESHOLD,
    AMOUNT_OVER_SUSPICIOUS_THRESHOLD,
    INVALID_AMOUNT,
    NOT_BLOCKED,
    NOT_SUSPICIOUS

}
