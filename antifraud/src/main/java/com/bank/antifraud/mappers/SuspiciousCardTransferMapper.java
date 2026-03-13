package com.bank.antifraud.mappers;

import com.bank.antifraud.dto.SuspiciousCardTransferDto;
import com.bank.antifraud.entity.SuspiciousCardTransfer;
import org.springframework.stereotype.Component;

@Component
public class SuspiciousCardTransferMapper {

    public SuspiciousCardTransfer toEntity(SuspiciousCardTransferDto dto) {
        if (dto == null) return null;

        SuspiciousCardTransfer entity = new SuspiciousCardTransfer();
        entity.setCardTransferId(dto.getCardTransferId());
        apply(dto, entity);
        return entity;
    }

    public void apply(SuspiciousCardTransferDto dto, SuspiciousCardTransfer entity) {
        entity.setIsBlocked(dto.getIsBlocked());
        entity.setIsSuspicious(dto.getIsSuspicious());
        entity.setBlockedReason(dto.getBlockedReason());
        entity.setSuspiciousReason(dto.getSuspiciousReason());
    }

    public SuspiciousCardTransferDto toDto(SuspiciousCardTransfer entity) {
        if (entity == null) return null;
        SuspiciousCardTransferDto dto = new SuspiciousCardTransferDto();
        dto.setCardTransferId(entity.getCardTransferId());
        dto.setIsBlocked(entity.getIsBlocked());
        dto.setIsSuspicious(entity.getIsSuspicious());
        dto.setBlockedReason(entity.getBlockedReason());
        dto.setSuspiciousReason(entity.getSuspiciousReason());
        return dto;
    }

}
