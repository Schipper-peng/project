package com.bank.antifraud.kafka.consumer;

import com.bank.antifraud.dto.AuditDto;
import com.bank.antifraud.mappers.AuditMapper;
import com.bank.antifraud.repository.AuditRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditConsumer {
    private final AuditRepository auditRepository;

    @KafkaListener(topics = "${app.kafka.topics.audit}")
    public void consume(AuditDto auditDto) {
        log.info("Received audit event: {}", auditDto);
        auditRepository.save(AuditMapper.toEntity(auditDto));
    }
}
