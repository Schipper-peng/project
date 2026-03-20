package com.bank.antifraud.kafka.consumer;

import com.bank.antifraud.dto.audit.AuditDto;
import com.bank.antifraud.entity.Audit;
import com.bank.antifraud.kafka.BaseKafkaSupport;
import com.bank.antifraud.kafka.KafkaTopics;
import com.bank.antifraud.mappers.AuditMapper;
import com.bank.antifraud.repository.AuditRepository;
import com.bank.antifraud.service.AuditService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class AuditConsumer extends BaseKafkaSupport {
    private final AuditService auditService;

    public AuditConsumer(AuditService auditService, ObjectMapper objectMapper) {
        super(objectMapper);
        this.auditService = auditService;
    }

    @KafkaListener(topics = KafkaTopics.AUDIT, groupId = "${spring.kafka.consumer.group-id}")
    public void listen(String payload) {
        AuditDto auditDto = readJson(payload, AuditDto.class);
        auditService.save(auditDto);
        log.info("Audit saved: entityType={}, operationType={}",
                auditDto.getEntityType(), auditDto.getOperationType());
    }

}
