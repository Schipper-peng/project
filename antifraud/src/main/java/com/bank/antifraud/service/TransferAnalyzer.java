package com.bank.antifraud.service;

import com.bank.antifraud.dto.analysis.FraudDecisionDto;
import com.bank.antifraud.dto.transfer.AccountTransferDto;
import com.bank.antifraud.dto.transfer.CardTransferDto;
import com.bank.antifraud.dto.transfer.PhoneTransferDto;

import java.math.BigDecimal;

public interface TransferAnalyzer {
    FraudDecisionDto analyzeAmount(BigDecimal amount);
    FraudDecisionDto analyzeAccount(AccountTransferDto dto);
    FraudDecisionDto analyzeCard(CardTransferDto dto);
    FraudDecisionDto analyzePhone(PhoneTransferDto dto);

}
