package com.bank.antifraud.mappers;

import com.bank.antifraud.dto.AuditDto;
import com.bank.antifraud.entity.Audit;
import com.bank.antifraud.enums.OperationType;
import com.bank.antifraud.enums.TransferType;
import org.springframework.stereotype.Component;

@Component
public final class AuditMapper {
    private AuditMapper() {
    }

    public AuditDto toDto(Audit audit) {
        if (audit == null) return null;

        AuditDto auditDto = new AuditDto();
        auditDto.setEntityType(TransferType.valueOf(audit.getEntityType()));
        auditDto.setOperationType(OperationType.valueOf(audit.getOperationType()));
        auditDto.setCreatedBy(audit.getCreatedBy());
        auditDto.setModifiedBy(audit.getModifiedBy());
        auditDto.setCreatedAt(audit.getCreatedAt());
        auditDto.setModifiedAt(audit.getModifiedAt());
        auditDto.setEntityJson(audit.getEntityJson());
        auditDto.setNewEntityJson(audit.getNewEntityJson());
        return auditDto;
    }

    public Audit toEntity(AuditDto auditDto) {
        if (auditDto == null) return null;
        Audit audit = new Audit();
        audit.setEntityType(String.valueOf(auditDto.getEntityType()));
        audit.setOperationType(String.valueOf(auditDto.getOperationType()));
        audit.setCreatedBy(auditDto.getCreatedBy());
        audit.setModifiedBy(auditDto.getModifiedBy());
        audit.setCreatedAt(auditDto.getCreatedAt());
        audit.setModifiedAt(auditDto.getModifiedAt());
        audit.setEntityJson(auditDto.getEntityJson());
        audit.setNewEntityJson(auditDto.getNewEntityJson());
        return audit;
    }

}
