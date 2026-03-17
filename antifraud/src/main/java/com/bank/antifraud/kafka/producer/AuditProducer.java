package com.bank.antifraud.kafka.producer;

import com.bank.antifraud.dto.AuditDto;
import com.bank.antifraud.kafka.KafkaTopics;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuditProducer {

    private final KafkaTemplate<String, AuditDto> kafkaTemplate;

    public void sendAudit(String key, AuditDto auditDto, String correlationId) {
        Message<AuditDto> message = MessageBuilder
                .withPayload(auditDto)
                .setHeader(KafkaHeaders.TOPIC, KafkaTopics.AUDIT)
                .setHeader(KafkaHeaders.KEY, key)
                .setHeader("correlationId", correlationId)
                .build();

        kafkaTemplate.send(message);
    }

}
