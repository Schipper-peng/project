package com.bank.antifraud.service;

import com.bank.antifraud.dto.audit.AuditDto;
import com.bank.antifraud.enums.OperationType;

public interface AuditService {
    AuditDto buildAudit(Object before, Object after, OperationType operationType);
    void save(AuditDto auditDto);

}
