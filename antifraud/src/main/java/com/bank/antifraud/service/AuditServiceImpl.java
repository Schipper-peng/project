package com.bank.antifraud.service;

import com.bank.antifraud.dto.AuditDto;
import com.bank.antifraud.enums.TransferType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

import static com.bank.antifraud.enums.OperationType.CREATE;
import static com.bank.antifraud.enums.OperationType.UPDATE;


@Service
@RequiredArgsConstructor
public class AuditServiceImpl implements AuditService {

    private final ObjectMapper objectMapper;

    @Override
    public AuditDto fromCreate(TransferType transferType, Object createdEntity, String createdBy) {
        return AuditDto.builder()
                .entityType(transferType)
                .operationType(CREATE)
                .createdBy(resolveUser(createdBy))
                .createdAt(Instant.now())
                .entityJson("{}")
                .newEntityJson(toJson(createdEntity))
                .build();
    }

    @Override
    public AuditDto fromUpdate(TransferType transferType, Object before, Object after, String modifiedBy) {
        return AuditDto.builder()
                .entityType(transferType)
                .operationType(UPDATE)
                .modifiedBy(resolveUser(modifiedBy))
                .modifiedAt(Instant.now())
                .entityJson(toJson(before))
                .newEntityJson(toJson(after))
                .build();
    }

    private String toJson(Object obj) {
        if (obj == null) {
            return "{}";
        }
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize audit entity to JSON", e);
        }
    }

    private String resolveUser(String username) {
        return (username == null || username.isBlank()) ? "SYSTEM" : username;
    }

}
