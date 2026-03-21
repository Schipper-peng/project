package com.bank.antifraud.mappers;

import com.bank.antifraud.dto.suspicious.SuspiciousPhoneTransferDto;
import com.bank.antifraud.entity.SuspiciousPhoneTransfer;
import com.bank.antifraud.enums.FraudReason;
import org.springframework.stereotype.Component;

@Component
public class SuspiciousPhoneTransferMapper {

    public SuspiciousPhoneTransfer toEntity(SuspiciousPhoneTransferDto dto) {
        if (dto == null) {
            return null;
        }
        return SuspiciousPhoneTransfer.builder()
                .id(dto.getId())
                .phoneTransferId(dto.getPhoneTransferId())
                .isBlocked(dto.getIsBlocked())
                .isSuspicious(dto.getIsSuspicious())
                .blockedReason(dto.getBlockedReason() != null ? dto.getBlockedReason().name() : null)
                .suspiciousReason(dto.getSuspiciousReason() != null ? dto.getSuspiciousReason().name() : null)
                .build();
    }

    public SuspiciousPhoneTransferDto toDto(SuspiciousPhoneTransfer entity) {
        if (entity == null) {
            return null;
        }
        return new SuspiciousPhoneTransferDto(
                entity.getId(),
                entity.getPhoneTransferId(),
                entity.getIsBlocked(),
                entity.getIsSuspicious(),
                entity.getBlockedReason() != null ? FraudReason.valueOf(entity.getBlockedReason()) : null ,
                entity.getSuspiciousReason() != null ? FraudReason.valueOf(entity.getSuspiciousReason()) : null
        );
    }

}
