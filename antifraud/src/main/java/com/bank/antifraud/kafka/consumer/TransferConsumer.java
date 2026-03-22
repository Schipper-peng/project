package com.bank.antifraud.kafka.consumer;

import com.bank.antifraud.dto.analysis.FraudDecisionDto;
import com.bank.antifraud.dto.suspicious.SuspiciousAccountTransferDto;
import com.bank.antifraud.dto.suspicious.SuspiciousCardTransferDto;
import com.bank.antifraud.dto.suspicious.SuspiciousPhoneTransferDto;
import com.bank.antifraud.dto.transfer.AccountTransferDto;
import com.bank.antifraud.dto.transfer.CardTransferDto;
import com.bank.antifraud.dto.transfer.PhoneTransferDto;
import com.bank.antifraud.enums.TransferType;
import com.bank.antifraud.kafka.KafkaTopics;
import com.bank.antifraud.kafka.producer.FraudDecisionProducer;
import com.bank.antifraud.kafka.producer.SuspiciousTransferProducer;
import com.bank.antifraud.service.TransferAnalyzer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransferConsumer {
    private final TransferAnalyzer transferAnalyzer;
    private final SuspiciousTransferProducer suspiciousTransferProducer;
    private final FraudDecisionProducer fraudDecisionProducer;


    @KafkaListener(topics = KafkaTopics.ACCOUNT_TRANSFER,
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "accountTransferKafkaListenerContainerFactory"
    )
    public void handleAccount(AccountTransferDto dto) {
        String corrId = UUID.randomUUID().toString();
        log.info("ACCOUNT dto before analyze: {}", dto);

        FraudDecisionDto decision = transferAnalyzer.analyzeAccount(dto);

        fraudDecisionProducer.sendDecision(decision, corrId);
        log.info("Fraud decision after analyze: {}", decision);
        if (Boolean.TRUE.equals(decision.isSuspicious()) || Boolean.TRUE.equals(decision.isBlocked())) {
            log.info("Sending suspicious create: {}", new SuspiciousAccountTransferDto(
                    null,
                    decision.getTransferId(),
                    decision.isBlocked(),
                    decision.isSuspicious(),
                    decision.getBlockedReason(),
                    decision.getSuspiciousReason()
            ));
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

    @KafkaListener(topics = KafkaTopics.CARD_TRANSFER,
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "cardTransferKafkaListenerContainerFactory"
    )
    public void handleCard(CardTransferDto dto) {
        String corrId = UUID.randomUUID().toString();

        FraudDecisionDto decision = transferAnalyzer.analyzeCard(dto);

        fraudDecisionProducer.sendDecision(decision, corrId);

        if (Boolean.TRUE.equals(decision.isSuspicious()) || Boolean.TRUE.equals(decision.isBlocked())) {
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

    @KafkaListener(topics = KafkaTopics.PHONE_TRANSFER,
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "phoneTransferKafkaListenerContainerFactory"
    )
    public void handlePhone(PhoneTransferDto dto) {
        String corrId = UUID.randomUUID().toString();

        FraudDecisionDto decision = transferAnalyzer.analyzePhone(dto);

        fraudDecisionProducer.sendDecision(decision, corrId);

        if (Boolean.TRUE.equals(decision.isSuspicious()) || Boolean.TRUE.equals(decision.isBlocked())) {
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
