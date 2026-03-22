package com.bank.antifraud.service.impl;

import com.bank.antifraud.dto.analysis.FraudDecisionDto;
import com.bank.antifraud.dto.transfer.AccountTransferDto;
import com.bank.antifraud.dto.transfer.CardTransferDto;
import com.bank.antifraud.dto.transfer.PhoneTransferDto;
import com.bank.antifraud.enums.FraudReason;
import com.bank.antifraud.enums.TransferType;
import com.bank.antifraud.service.TransferAnalyzer;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class TransferAnalyzerImpl implements TransferAnalyzer {
    private static final BigDecimal BLOCK_THRESHOLD = BigDecimal.valueOf(500_000);
    private static final BigDecimal SUSPICIOUS_THRESHOLD = BigDecimal.valueOf(100_000);

    @Override
    public FraudDecisionDto analyzeAmount(BigDecimal amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Amount must not be null");
        }

        if (amount.compareTo(BLOCK_THRESHOLD) > 0) {
            return new FraudDecisionDto(
                    null,
                    null,
                    true,
                    false,
                    FraudReason.AMOUNT_OVER_BLOCK_THRESHOLD,
                    FraudReason.NOT_SUSPICIOUS
            );
        }

        if (amount.compareTo(SUSPICIOUS_THRESHOLD) > 0) {
            return new FraudDecisionDto(
                    null,
                    null,
                    false,
                    true,
                    FraudReason.NOT_BLOCKED,
                    FraudReason.AMOUNT_OVER_SUSPICIOUS_THRESHOLD
            );
        }

        return new FraudDecisionDto(
                null,
                null,
                false,
                false,
                FraudReason.NOT_BLOCKED,
                FraudReason.NOT_SUSPICIOUS
        );
    }

    @Override
    public FraudDecisionDto analyzeAccount(AccountTransferDto dto) {
        FraudDecisionDto decision = analyzeAmount(dto.getAmount());
        decision.setTransferId(dto.getAccountTransferId());
        decision.setTransferType(TransferType.ACCOUNT);
        return decision;
    }

    @Override
    public FraudDecisionDto analyzeCard(CardTransferDto dto) {
        FraudDecisionDto decision = analyzeAmount(dto.getAmount());
        decision.setTransferId(dto.getCardTransferId());
        decision.setTransferType(TransferType.CARD);
        return decision;
    }

    @Override
    public FraudDecisionDto analyzePhone(PhoneTransferDto dto) {
        FraudDecisionDto decision = analyzeAmount(dto.getAmount());
        decision.setTransferId(dto.getPhoneTransferId());
        decision.setTransferType(TransferType.PHONE);
        return decision;
    }

}
