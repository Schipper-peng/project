package com.bank.antifraud.mappers;


import com.bank.antifraud.dto.SuspiciousAccountTransferDto;
import com.bank.antifraud.entity.SuspiciousAccountTransfer;
import org.springframework.stereotype.Component;

@Component
public class SuspiciousAccountTransferMapper {

    public SuspiciousAccountTransfer toEntity(SuspiciousAccountTransferDto dto) {
        if (dto == null) return null;

        SuspiciousAccountTransfer entity = new SuspiciousAccountTransfer();
        entity.setAccountTransferId(dto.getAccountTransferId());
        apply(dto, entity);
        return entity;
    }

    public void apply(SuspiciousAccountTransferDto dto, SuspiciousAccountTransfer entity) {
        entity.setIsBlocked(dto.getIsBlocked());
        entity.setIsSuspicious(dto.getIsSuspicious());
        entity.setBlockedReason(dto.getBlockedReason());
        entity.setSuspiciousReason(dto.getSuspiciousReason());
    }

    public SuspiciousAccountTransferDto toDto(SuspiciousAccountTransfer entity) {
        if (entity == null) return null;
        SuspiciousAccountTransferDto dto = new SuspiciousAccountTransferDto();
        dto.setAccountTransferId(entity.getAccountTransferId());
        dto.setIsBlocked(entity.getIsBlocked());
        dto.setIsSuspicious(entity.getIsSuspicious());
        dto.setBlockedReason(entity.getBlockedReason());
        dto.setSuspiciousReason(entity.getSuspiciousReason());
        return dto;
    }

}
