package com.bank.antifraud.kafka.consumer;

import com.bank.antifraud.dto.AuditDto;
import com.bank.antifraud.entity.Audit;
import com.bank.antifraud.mappers.AuditMapper;
import com.bank.antifraud.repository.AuditRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditConsumer {
    private final AuditRepository auditRepository;
    private final AuditMapper auditMapper;

    @KafkaListener(topics = "${app.kafka.topics.audit}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    @Transactional
    public void consume(AuditDto auditDto) {
        log.info("Received audit : {}", auditDto);

        Audit audit = auditMapper.toEntity(auditDto);
        auditRepository.save(audit);

        log.info("Audit event saved successfully");
    }

}
