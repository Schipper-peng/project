package com.bank.antifraud.kafka.consumer;

import com.bank.antifraud.dto.analysis.FraudDecisionDto;
import com.bank.antifraud.dto.suspicious.SuspiciousAccountTransferDto;
import com.bank.antifraud.dto.suspicious.SuspiciousCardTransferDto;
import com.bank.antifraud.dto.suspicious.SuspiciousPhoneTransferDto;
import com.bank.antifraud.dto.transfer.AccountTransferDto;
import com.bank.antifraud.dto.transfer.CardTransferDto;
import com.bank.antifraud.dto.transfer.PhoneTransferDto;
import com.bank.antifraud.enums.TransferType;
import com.bank.antifraud.kafka.BaseKafkaSupport;
import com.bank.antifraud.kafka.KafkaTopics;
import com.bank.antifraud.kafka.producer.FraudDecisionProducer;
import com.bank.antifraud.kafka.producer.SuspiciousTransferProducer;
import com.bank.antifraud.service.TransferAnalyzer;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class TransferConsumer extends BaseKafkaSupport {
    private final TransferAnalyzer transferAnalyzer;
    private final SuspiciousTransferProducer suspiciousTransferProducer;
    private final FraudDecisionProducer fraudDecisionProducer;

    public TransferConsumer(TransferAnalyzer transferAnalyzer,
                            SuspiciousTransferProducer suspiciousTransferProducer,
                            FraudDecisionProducer fraudDecisionProducer,
                            ObjectMapper objectMapper) {
        super(objectMapper);
        this.transferAnalyzer = transferAnalyzer;
        this.suspiciousTransferProducer = suspiciousTransferProducer;
        this.fraudDecisionProducer = fraudDecisionProducer;
    }



    @KafkaListener(topics = KafkaTopics.ACCOUNT_TRANSFER, groupId = "${spring.kafka.consumer.group-id}")
    public void handleAccount(String payload) {
        String corrId = UUID.randomUUID().toString();
        AccountTransferDto dto = readJson(payload, AccountTransferDto.class);

        FraudDecisionDto decision = transferAnalyzer.analyzeAccount(dto);

        fraudDecisionProducer.sendDecision(decision, corrId);

        if (Boolean.TRUE.equals(decision.isSuspicious())) {
            suspiciousTransferProducer.sendCreate(
                    new SuspiciousAccountTransferDto(
                            null,
                            decision.getTransferId(),
                            decision.isBlocked(),
                            decision.isSuspicious(),
                            decision.getBlockedReason(),
                            decision.getSuspiciousReason()
                    ),
                    TransferType.ACCOUNT,
                    corrId
            );
        }

        log.info("Account transfer processed: transferId={}, suspicious={}",
                dto.getAccountTransferId(), decision.isSuspicious());
    }

    @KafkaListener(topics = KafkaTopics.CARD_TRANSFER, groupId = "${spring.kafka.consumer.group-id}")
    public void handleCard(String payload) {
        String corrId = UUID.randomUUID().toString();
        CardTransferDto dto = readJson(payload, CardTransferDto.class);

        FraudDecisionDto decision = transferAnalyzer.analyzeCard(dto);

        fraudDecisionProducer.sendDecision(decision, corrId);

        if (Boolean.TRUE.equals(decision.isSuspicious())) {
            suspiciousTransferProducer.sendCreate(
                    new SuspiciousCardTransferDto(
                            null,
                            decision.getTransferId(),
                            decision.isBlocked(),
                            decision.isSuspicious(),
                            decision.getBlockedReason(),
                            decision.getSuspiciousReason()
                    ),
                    TransferType.CARD,
                    corrId
            );
        }

        log.info("Card transfer processed: transferId={}, suspicious={}",
                dto.getCardTransferId(), decision.isSuspicious());
    }

    @KafkaListener(topics = KafkaTopics.PHONE_TRANSFER, groupId = "${spring.kafka.consumer.group-id}")
    public void handlePhone(String payload) {
        String corrId = UUID.randomUUID().toString();
        PhoneTransferDto dto = readJson(payload, PhoneTransferDto.class);

        FraudDecisionDto decision = transferAnalyzer.analyzePhone(dto);

        fraudDecisionProducer.sendDecision(decision, corrId);

        if (Boolean.TRUE.equals(decision.isSuspicious())) {
            suspiciousTransferProducer.sendCreate(
                    new SuspiciousPhoneTransferDto(
                            null,
                            decision.getTransferId(),
                            decision.isBlocked(),
                            decision.isSuspicious(),
                            decision.getBlockedReason(),
                            decision.getSuspiciousReason()
                    ),
                    TransferType.PHONE,
                    corrId
            );
        }

        log.info("Phone transfer processed: transferId={}, suspicious={}",
                dto.getPhoneTransferId(), decision.isSuspicious());
    }

}
