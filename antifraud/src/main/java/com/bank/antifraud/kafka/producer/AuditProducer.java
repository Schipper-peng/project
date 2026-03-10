package com.bank.antifraud.kafka.producer;

import com.bank.antifraud.dto.AuditDto;
import com.bank.antifraud.kafka.KafkaTopics;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuditProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void send(AuditDto auditDto) {
        kafkaTemplate.send(KafkaTopics.AUDIT, auditDto.getEntityType(), auditDto);
    }
}
