package com.bank.antifraud.mappers;


import com.bank.antifraud.dto.suspicious.SuspiciousAccountTransferDto;
import com.bank.antifraud.entity.SuspiciousAccountTransfer;
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
                .blockedReason(dto.getBlockedReason())
                .suspiciousReason(dto.getSuspiciousReason())
                .build();
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
                entity.getBlockedReason(),
                entity.getSuspiciousReason()
        );
    }

}
