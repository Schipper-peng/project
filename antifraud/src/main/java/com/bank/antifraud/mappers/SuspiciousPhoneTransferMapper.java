package com.bank.antifraud.mappers;

import com.bank.antifraud.dto.SuspiciousPhoneTransferDto;
import com.bank.antifraud.entity.SuspiciousPhoneTransfer;
import org.springframework.stereotype.Component;

@Component
public class SuspiciousPhoneTransferMapper {
    public SuspiciousPhoneTransfer toEntity(SuspiciousPhoneTransferDto dto) {
        if (dto == null) return null;

        SuspiciousPhoneTransfer entity = new SuspiciousPhoneTransfer();
        entity.setPhoneTransferId(dto.getPhoneTransferId());
        apply(dto, entity);
        return entity;
    }

    public void apply(SuspiciousPhoneTransferDto dto, SuspiciousPhoneTransfer entity) {
        entity.setIsBlocked(dto.getIsBlocked());
        entity.setIsSuspicious(dto.getIsSuspicious());
        entity.setBlockedReason(dto.getBlockedReason());
        entity.setSuspiciousReason(dto.getSuspiciousReason());
    }
    public SuspiciousPhoneTransferDto toDto(SuspiciousPhoneTransfer entity) {
        if (entity == null) return null;
        SuspiciousPhoneTransferDto dto = new SuspiciousPhoneTransferDto();
        dto.setPhoneTransferId(entity.getPhoneTransferId());
        dto.setIsBlocked(entity.getIsBlocked());
        dto.setIsSuspicious(entity.getIsSuspicious());
        dto.setBlockedReason(entity.getBlockedReason());
        dto.setSuspiciousReason(entity.getSuspiciousReason());
        return dto;
    }
}
