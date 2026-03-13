package com.bank.antifraud.service;

import com.bank.antifraud.dto.AuditDto;
import com.bank.antifraud.kafka.producer.AuditProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AuditServiceImpl implements AuditService {

    private final AuditProducer auditProducer;

    @Override
    public void send(AuditDto auditDto) {
        auditProducer.send(auditDto);
    }

}
