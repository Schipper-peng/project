package com.bank.antifraud.service;

import com.bank.antifraud.dto.audit.AuditDto;
import com.bank.antifraud.enums.OperationType;
import com.bank.antifraud.enums.TransferType;

public interface AuditService {
    AuditDto buildAudit(Object before, Object after, OperationType operationType);
    void save(AuditDto auditDto);

}
