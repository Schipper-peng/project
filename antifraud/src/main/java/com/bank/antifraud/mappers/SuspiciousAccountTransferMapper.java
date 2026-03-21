package com.bank.antifraud.mappers;


import com.bank.antifraud.dto.suspicious.SuspiciousAccountTransferDto;
import com.bank.antifraud.entity.SuspiciousAccountTransfer;
import com.bank.antifraud.enums.FraudReason;
import org.springframework.stereotype.Component;

@Component
public class SuspiciousAccountTransferMapper {

    public SuspiciousAccountTransfer toEntity(SuspiciousAccountTransferDto dto) {
        if (dto == null) {
            return null;
        }
        return SuspiciousAccountTransfer.builder()
                .id(dto.getId())
                .accountTransferId(dto.getAccountTransferId())
                .isBlocked(dto.getIsBlocked())
                .isSuspicious(dto.getIsSuspicious())
                .blockedReason(dto.getBlockedReason() != null ? dto.getBlockedReason().name() : null)
                .suspiciousReason(dto.getSuspiciousReason() != null ? dto.getSuspiciousReason().name() : null)
                .build();
    }

    public void updateEntityFromDto(SuspiciousAccountTransferDto dto, SuspiciousAccountTransfer entity) {
        entity.setAccountTransferId(dto.getAccountTransferId());
        entity.setIsBlocked(dto.getIsBlocked());
        entity.setIsSuspicious(dto.getIsSuspicious());
        entity.setBlockedReason(dto.getBlockedReason() != null ? dto.getBlockedReason().name() : null);
        entity.setSuspiciousReason(dto.getSuspiciousReason() != null ? dto.getSuspiciousReason().name() : null);
    }

    public SuspiciousAccountTransferDto toDto(SuspiciousAccountTransfer entity) {
        if (entity == null) {
            return null;
        }
        return new SuspiciousAccountTransferDto(
                entity.getId(),
                entity.getAccountTransferId(),
                entity.getIsBlocked(),
                entity.getIsSuspicious(),
                entity.getBlockedReason() != null ? FraudReason.valueOf(entity.getBlockedReason()) : null,
                entity.getSuspiciousReason() != null ? FraudReason.valueOf(entity.getSuspiciousReason()) : null
        );
    }

}
