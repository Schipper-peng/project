package com.bank.antifraud.mappers;

import com.bank.antifraud.dto.AuditDto;
import com.bank.antifraud.entity.Audit;
import org.springframework.stereotype.Component;

public final class AuditMapper {
    private AuditMapper() {}

    public static AuditDto toDto(Audit audit) {
        if (audit == null) return null;

        AuditDto auditDto = new AuditDto();
        auditDto.setEntityType(audit.getEntityType());
        auditDto.setOperationType(audit.getOperationType());
        auditDto.setCreatedBy(audit.getCreatedBy());
        auditDto.setModifiedBy(audit.getModifiedBy());
        auditDto.setCreatedAt(audit.getCreatedAt());
        auditDto.setModifiedAt(audit.getModifiedAt());
        auditDto.setEntityJson(audit.getEntityJson());
        auditDto.setNewEntityJson(audit.getNewEntityJson());
        return auditDto;
    }
    public static Audit toEntity(AuditDto auditDto) {
        if (auditDto == null) return null;
        Audit audit = new Audit();
        audit.setEntityType(auditDto.getEntityType());
        audit.setOperationType(auditDto.getOperationType());
        audit.setCreatedBy(auditDto.getCreatedBy());
        audit.setModifiedBy(auditDto.getModifiedBy());
        audit.setCreatedAt(auditDto.getCreatedAt());
        audit.setModifiedAt(auditDto.getModifiedAt());
        audit.setEntityJson(auditDto.getEntityJson());
        audit.setNewEntityJson(auditDto.getNewEntityJson());
        return audit;
    }
}
