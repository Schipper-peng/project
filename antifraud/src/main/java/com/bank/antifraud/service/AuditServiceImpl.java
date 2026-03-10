package com.bank.antifraud.service;

import com.bank.antifraud.dto.AuditDto;
import com.bank.antifraud.entity.Audit;
import com.bank.antifraud.enums.TransferType;
import com.bank.antifraud.kafka.producer.AuditProducer;
import com.bank.antifraud.repository.AuditRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
@Service
@RequiredArgsConstructor
public class AuditServiceImpl implements AuditService {

    private final AuditProducer auditProducer;

    @Override
    public void send(AuditDto auditDto) {
        auditProducer.send(auditDto);
    }

}
