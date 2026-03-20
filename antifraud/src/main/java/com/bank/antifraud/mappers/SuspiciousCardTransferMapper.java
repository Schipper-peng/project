package com.bank.antifraud.mappers;

import com.bank.antifraud.dto.suspicious.SuspiciousCardTransferDto;
import com.bank.antifraud.entity.SuspiciousCardTransfer;
import org.springframework.stereotype.Component;

@Component
public class SuspiciousCardTransferMapper {

    public SuspiciousCardTransfer toEntity(SuspiciousCardTransferDto dto) {
        if (dto == null) {
            return null;
        }
        return SuspiciousCardTransfer.builder()
                .id(dto.getId())
                .cardTransferId(dto.getCardTransferId())
                .isBlocked(dto.getIsBlocked())
                .isSuspicious(dto.getIsSuspicious())
                .blockedReason(dto.getBlockedReason())
                .suspiciousReason(dto.getSuspiciousReason())
                .build();
    }

    public SuspiciousCardTransferDto toDto(SuspiciousCardTransfer entity) {
        if (entity == null) {
            return null;
        }
        return new SuspiciousCardTransferDto(
                entity.getId(),
                entity.getCardTransferId(),
                entity.getIsBlocked(),
                entity.getIsSuspicious(),
                entity.getBlockedReason(),
                entity.getSuspiciousReason()
        );
    }

}
