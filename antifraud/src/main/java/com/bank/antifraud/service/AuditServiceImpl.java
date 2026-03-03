package com.bank.antifraud.service;

import com.bank.antifraud.entity.Audit;
import com.bank.antifraud.enums.TransferType;
import com.bank.antifraud.repository.AuditRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
@Service
@RequiredArgsConstructor
public class AuditServiceImpl implements AuditService {

    private final AuditRepository auditRepository;

    @Override
    public void log(TransferType entityType, String operationType, String entityJson, String newEntityJson) {
        Audit audit =  new Audit();
        audit.setEntityType(String.valueOf(entityType));
        audit.setOperationType(operationType);

        String user = getCurrentUser();
        audit.setCreatedBy(user);
        audit.setCreatedAt(Instant.now());
        audit.setModifiedBy(user);
        audit.setModifiedAt(Instant.now());

        audit.setEntityJson(entityJson);
        audit.setNewEntityJson(newEntityJson);
        auditRepository.save(audit);
    }

    private String getCurrentUser() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return "system";
        return auth.getName();
    }
}
