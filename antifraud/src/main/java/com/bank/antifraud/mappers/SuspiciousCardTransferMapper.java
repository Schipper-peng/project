package com.bank.antifraud.mappers;

import com.bank.antifraud.dto.suspicious.SuspiciousCardTransferDto;
import com.bank.antifraud.entity.SuspiciousCardTransfer;
import com.bank.antifraud.enums.FraudReason;
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
                .blockedReason(dto.getBlockedReason() != null ? dto.getBlockedReason().name() : null)
                .suspiciousReason(dto.getSuspiciousReason() != null ? dto.getSuspiciousReason().name() : null)
                .build();
    }

    public void updateEntityFromDto(SuspiciousCardTransferDto dto, SuspiciousCardTransfer entity) {
        entity.setCardTransferId(dto.getCardTransferId());
        entity.setIsBlocked(dto.getIsBlocked());
        entity.setIsSuspicious(dto.getIsSuspicious());
        entity.setBlockedReason(dto.getBlockedReason() != null ? dto.getBlockedReason().name() : null);
        entity.setSuspiciousReason(dto.getSuspiciousReason() != null ? dto.getSuspiciousReason().name() : null);
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
                entity.getBlockedReason() != null ? FraudReason.valueOf(entity.getBlockedReason()) : null,
                entity.getSuspiciousReason() != null ? FraudReason.valueOf(entity.getSuspiciousReason()) : null
        );
    }

}
