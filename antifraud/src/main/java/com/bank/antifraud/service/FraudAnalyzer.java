package com.bank.antifraud.service;

import com.bank.antifraud.enums.FraudReason;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;


@Component
public class FraudAnalyzer {

    private static final BigDecimal BLOCK_THRESHOLD = new BigDecimal("500000");
    private static final BigDecimal SUSPICIOUS_THRESHOLD = new BigDecimal("100000");

    public FraudDecision analyze(BigDecimal amount) {

        if (amount.compareTo(BLOCK_THRESHOLD) > 0) {
            return new FraudDecision(
                    true,
                    false,
                    FraudReason.AMOUNT_OVER_BLOCK_THRESHOLD,
                    FraudReason.NOT_SUSPICIOUS
            );
        }

        if (amount.compareTo(SUSPICIOUS_THRESHOLD) > 0) {
            return new FraudDecision(
                    false,
                    true,
                    FraudReason.NOT_BLOCKED,
                    FraudReason.AMOUNT_OVER_SUSPICIOUS_THRESHOLD
            );
        }

        return new FraudDecision(
                false,
                false,
                FraudReason.NOT_BLOCKED,
                FraudReason.NOT_SUSPICIOUS
        );
    }
}
