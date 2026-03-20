package com.bank.antifraud.kafka.consumer;

import com.bank.antifraud.dto.SuspiciousIdDto;
import com.bank.antifraud.dto.suspicious.SuspiciousAccountTransferDto;
import com.bank.antifraud.dto.suspicious.SuspiciousCardTransferDto;
import com.bank.antifraud.dto.suspicious.SuspiciousPhoneTransferDto;
import com.bank.antifraud.enums.OperationType;
import com.bank.antifraud.enums.TransferType;
import com.bank.antifraud.kafka.BaseKafkaSupport;
import com.bank.antifraud.kafka.KafkaHeader;
import com.bank.antifraud.kafka.KafkaTopics;
import com.bank.antifraud.service.SuspiciousTransferService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Slf4j
@Service
public class SuspiciousTransferConsumer extends BaseKafkaSupport {
    private final SuspiciousTransferService suspiciousTransferService;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public SuspiciousTransferConsumer(SuspiciousTransferService suspiciousTransferService,
                                      KafkaTemplate<String, String> kafkaTemplate,
                                      ObjectMapper objectMapper) {
        super(objectMapper);
        this.suspiciousTransferService = suspiciousTransferService;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(
            topics = {KafkaTopics.CREATE, KafkaTopics.UPDATE, KafkaTopics.DELETE, KafkaTopics.GET},
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void listen(ConsumerRecord<String, String> record) {
        String correlationId = correlationId(record.headers());

        withMdc(correlationId, () -> {
            TransferType transferType = TransferType.valueOf(header(record.headers(), KafkaHeader.TRANSFER_TYPE));
            OperationType operationType = OperationType.valueOf(header(record.headers(), KafkaHeader.OPERATION_TYPE));
            String replyTopic = header(record.headers(), KafkaHeader.REPLY_TOPIC);

            switch (operationType) {
                case CREATE -> handleCreate(record.value(), transferType);
                case UPDATE -> handleUpdate(record.value(), transferType);
                case DELETE -> handleDelete(record.value(), transferType);
                case GET -> handleGet(record.value(), transferType, correlationId, replyTopic);
                default -> throw new IllegalArgumentException("Unsupported operation: " + operationType);
            }

            log.info("SuspiciousTransferConsumer processed: operation={}, transferType={}",
                    operationType, transferType);
        });
    }

    private void handleCreate(String payload, TransferType transferType) {
        switch (transferType) {
            case ACCOUNT -> suspiciousTransferService.createAccount(readJson(payload, SuspiciousAccountTransferDto.class));
            case CARD -> suspiciousTransferService.createCard(readJson(payload, SuspiciousCardTransferDto.class));
            case PHONE -> suspiciousTransferService.createPhone(readJson(payload, SuspiciousPhoneTransferDto.class));
        }
    }

    private void handleUpdate(String payload, TransferType transferType) {
        switch (transferType) {
            case ACCOUNT -> suspiciousTransferService.updateAccount(readJson(payload, SuspiciousAccountTransferDto.class));
            case CARD -> suspiciousTransferService.updateCard(readJson(payload, SuspiciousCardTransferDto.class));
            case PHONE -> suspiciousTransferService.updatePhone(readJson(payload, SuspiciousPhoneTransferDto.class));
        }
    }

    private void handleDelete(String payload, TransferType transferType) {
        Long id = readJson(payload, SuspiciousIdDto.class).getId();

        switch (transferType) {
            case ACCOUNT -> suspiciousTransferService.deleteAccount(id);
            case CARD -> suspiciousTransferService.deleteCard(id);
            case PHONE -> suspiciousTransferService.deletePhone(id);
        }
    }

    private void handleGet(String payload, TransferType transferType, String correlationId, String replyTopic) {
        Long id = readJson(payload, SuspiciousIdDto.class).getId();

        Object response = switch (transferType) {
            case ACCOUNT -> suspiciousTransferService.getAccount(id);
            case CARD -> suspiciousTransferService.getCard(id);
            case PHONE -> suspiciousTransferService.getPhone(id);
        };

        String targetTopic = replyTopic != null ? replyTopic : KafkaTopics.GET_REPLY;

        ProducerRecord<String, String> replyRecord =
                new ProducerRecord<>(targetTopic, String.valueOf(id), writeJson(response));

        replyRecord.headers().add(
                KafkaHeader.CORRELATION_ID,
                correlationId.getBytes(StandardCharsets.UTF_8)
        );
        replyRecord.headers().add(
                KafkaHeader.TRANSFER_TYPE,
                transferType.name().getBytes(StandardCharsets.UTF_8)
        );
        replyRecord.headers().add(
                KafkaHeader.OPERATION_TYPE,
                OperationType.GET.name().getBytes(StandardCharsets.UTF_8)
        );

        kafkaTemplate.send(replyRecord);
        log.info("GET reply sent: topic={}, id={}, transferType={}", targetTopic, id, transferType);
    }

}
