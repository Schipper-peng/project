package com.bank.antifraud.kafka.producer;

import com.bank.antifraud.dto.audit.AuditDto;
import com.bank.antifraud.kafka.BaseKafkaSupport;
import com.bank.antifraud.kafka.KafkaHeader;
import com.bank.antifraud.kafka.KafkaTopics;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Slf4j
@Service
public class AuditProducer extends BaseKafkaSupport {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public AuditProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        super(objectMapper);
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendAudit(String key, AuditDto auditDto, String correlationId) {
        ProducerRecord<String, String> record =
                new ProducerRecord<>(KafkaTopics.AUDIT, key, writeJson(auditDto));

        if (correlationId != null) {
            record.headers().add(
                    KafkaHeader.CORRELATION_ID,
                    correlationId.getBytes(StandardCharsets.UTF_8)
            );
        }

        kafkaTemplate.send(record);
        log.info("Audit message sent: key={}", key);
    }

}
