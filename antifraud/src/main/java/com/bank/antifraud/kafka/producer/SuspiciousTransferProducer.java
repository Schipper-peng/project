package com.bank.antifraud.kafka.producer;


import com.bank.antifraud.dto.SuspiciousIdDto;
import com.bank.antifraud.enums.OperationType;
import com.bank.antifraud.enums.TransferType;
import com.bank.antifraud.kafka.BaseKafkaSupport;
import com.bank.antifraud.kafka.KafkaHeader;
import com.bank.antifraud.kafka.KafkaTopics;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Slf4j
@Service
public class SuspiciousTransferProducer extends BaseKafkaSupport {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public SuspiciousTransferProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        super(objectMapper);
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendCreate(Object dto, TransferType transferType, String correlationId) {
        send(KafkaTopics.CREATE, dto, transferType, OperationType.CREATE, correlationId, null, null);
    }

    public void sendUpdate(Object dto, TransferType transferType, String correlationId) {
        send(KafkaTopics.UPDATE, dto, transferType, OperationType.UPDATE, correlationId, null, null);
    }

    public void sendDelete(Long id, TransferType transferType, String correlationId) {
        send(KafkaTopics.DELETE, new SuspiciousIdDto(id), transferType, OperationType.DELETE, correlationId, String.valueOf(id), null);
    }

    public void sendGet(Long id, TransferType transferType, String correlationId) {
        send(KafkaTopics.GET, new SuspiciousIdDto(id), transferType, OperationType.GET, correlationId, String.valueOf(id), KafkaTopics.GET_REPLY);
    }

    private void send(String topic,
                      Object payload,
                      TransferType transferType,
                      OperationType operationType,
                      String correlationId,
                      String entityId,
                      String replyTopic) {

        ProducerRecord<String, String> record =
                new ProducerRecord<>(topic, entityId, writeJson(payload));

        record.headers().add(
                KafkaHeader.TRANSFER_TYPE,
                transferType.name().getBytes(StandardCharsets.UTF_8)
        );
        record.headers().add(
                KafkaHeader.OPERATION_TYPE,
                operationType.name().getBytes(StandardCharsets.UTF_8)
        );

        if (correlationId != null) {
            record.headers().add(
                    KafkaHeader.CORRELATION_ID,
                    correlationId.getBytes(StandardCharsets.UTF_8)
            );
        }

        if (entityId != null) {
            record.headers().add(
                    KafkaHeader.ENTITY_ID,
                    entityId.getBytes(StandardCharsets.UTF_8)
            );
        }

        if (replyTopic != null) {
            record.headers().add(
                    KafkaHeader.REPLY_TOPIC,
                    replyTopic.getBytes(StandardCharsets.UTF_8)
            );
        }

        kafkaTemplate.send(record);
        log.info("Message sent to topic={}, operation={}, transferType={}", topic, operationType, transferType);
    }

}

