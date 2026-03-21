package com.bank.antifraud.service.impl;

import com.bank.antifraud.dto.audit.AuditDto;
import com.bank.antifraud.dto.suspicious.SuspiciousAccountTransferDto;
import com.bank.antifraud.dto.suspicious.SuspiciousCardTransferDto;
import com.bank.antifraud.dto.suspicious.SuspiciousPhoneTransferDto;
import com.bank.antifraud.entity.Audit;
import com.bank.antifraud.enums.OperationType;
import com.bank.antifraud.enums.TransferType;
import com.bank.antifraud.mappers.AuditMapper;
import com.bank.antifraud.repository.AuditRepository;
import com.bank.antifraud.service.AuditService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;


@Service
@RequiredArgsConstructor
public class AuditServiceImpl implements AuditService {

    private final AuditRepository auditRepository;
    private final AuditMapper auditMapper;
    private final ObjectMapper objectMapper;

    @Override
    public AuditDto buildAudit(Object before, Object after, OperationType operationType) {
        Instant now = Instant.now();

        boolean isCreate = operationType == OperationType.CREATE;
        boolean isUpdate = operationType == OperationType.UPDATE;

        return AuditDto.builder()
                .entityType(resolveEntityType(before, after))
                .operationType(operationType)
                .createdBy(isCreate ? "system" : null)
                .modifiedBy(isUpdate ? "system" : null)
                .createdAt(isCreate ? now : null)
                .modifiedAt(isUpdate ? now : null)
                .entityJson(toJson(before))
                .newEntityJson(toJson(after))
                .build();
    }

    @Override
    public void save(AuditDto auditDto) {
        Audit audit = auditMapper.toEntity(auditDto);
        auditRepository.save(audit);
    }

    private TransferType resolveEntityType(Object before, Object after) {
        Object target = after != null ? after : before;

        if (target instanceof SuspiciousAccountTransferDto) {
            return TransferType.ACCOUNT;
        }
        if (target instanceof SuspiciousCardTransferDto) {
            return TransferType.CARD;
        }
        if (target instanceof SuspiciousPhoneTransferDto) {
            return TransferType.PHONE;
        }
        throw new IllegalArgumentException("Unsupported audit entity type: " + target);
    }

    private String toJson(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize object for audit", e);
        }
    }
}
