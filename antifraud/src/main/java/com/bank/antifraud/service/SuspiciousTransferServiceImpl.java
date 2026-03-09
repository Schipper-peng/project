package com.bank.antifraud.service;

import com.bank.antifraud.dto.SuspiciousAccountTransferDto;
import com.bank.antifraud.dto.SuspiciousCardTransferDto;
import com.bank.antifraud.dto.SuspiciousPhoneTransferDto;
import com.bank.antifraud.entity.SuspiciousAccountTransfer;
import com.bank.antifraud.entity.SuspiciousCardTransfer;
import com.bank.antifraud.entity.SuspiciousPhoneTransfer;
import com.bank.antifraud.enums.TransferType;
import com.bank.antifraud.kafka.dto.SuspiciousTransferCommand;
import com.bank.antifraud.mappers.SuspiciousAccountTransferMapper;
import com.bank.antifraud.mappers.SuspiciousCardTransferMapper;
import com.bank.antifraud.mappers.SuspiciousPhoneTransferMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import com.bank.antifraud.repository.SuspiciousAccountTransferRepository;
import com.bank.antifraud.repository.SuspiciousCardTransferRepository;
import com.bank.antifraud.repository.SuspiciousPhoneTransferRepository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;


@Service
@Transactional
@RequiredArgsConstructor
public class SuspiciousTransferServiceImpl implements  SuspiciousTransferService {

    private final SuspiciousAccountTransferRepository accountRepository;
    private final SuspiciousCardTransferRepository cardRepository;
    private final SuspiciousPhoneTransferRepository phoneRepository;

    private final SuspiciousCardTransferMapper cardMapper;
    private final SuspiciousAccountTransferMapper accountMapper;
    private final SuspiciousPhoneTransferMapper phoneMapper;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private final AuditService auditService;


    public SuspiciousCardTransferDto upsertCard(SuspiciousCardTransferDto dto) {
        if (dto.getCardTransferId() == null) {
            throw new IllegalArgumentException("Card Transfer Id is required");
        }
        SuspiciousCardTransfer entity = cardRepository.findByCardTransferId(dto.getCardTransferId())
                .map(existing -> {
                    cardMapper.apply(dto, existing);
                    return existing;
                })
                .orElseGet(() -> cardMapper.toEntity(dto));
        SuspiciousCardTransfer saved = cardRepository.save(entity);
        return cardMapper.toDto(saved);
    }

    public SuspiciousPhoneTransferDto upsertPhone(SuspiciousPhoneTransferDto dto) {
        if (dto.getPhoneTransferId() == null) {
            throw new IllegalArgumentException("Phone Transfer Id is required");
        }
        SuspiciousPhoneTransfer entity = phoneRepository.findByPhoneTransferId(dto.getPhoneTransferId())
                .map(existing -> {
                    phoneMapper.apply(dto, existing);
                    return existing;
                })
                .orElseGet(() -> phoneMapper.toEntity(dto));
        SuspiciousPhoneTransfer saved = phoneRepository.save(entity);
        return phoneMapper.toDto(saved);
    }

    public SuspiciousAccountTransferDto upsertAccount(SuspiciousAccountTransferDto dto) {
        if (dto.getAccountTransferId() == null) {
            throw new IllegalArgumentException("Account Transfer Id is required");
        }
        SuspiciousAccountTransfer entity = accountRepository.findByAccountTransferId(dto.getAccountTransferId())
                .map(existing -> {
                    accountMapper.apply(dto, existing);
                    return existing;
                })
                .orElseGet(() -> accountMapper.toEntity(dto));
        SuspiciousAccountTransfer saved = accountRepository.save(entity);
        return accountMapper.toDto(saved);
    }

    @Override
    public void handleCreate(SuspiciousTransferCommand cmd) {
        handleUpsert(cmd);
    }

    @Override
    public void handleUpdate(SuspiciousTransferCommand cmd) {
        handleUpsert(cmd);
    }

    @Override
    public void handleDelete(SuspiciousTransferCommand cmd) {
        if (cmd == null || cmd.getTransferType() == null || cmd.getTransferId() == null) {
            throw new IllegalArgumentException("Invalid delete command");
        }
        switch (cmd.getTransferType()) {
            case CARD -> cardRepository.deleteByCardTransferId(cmd.getTransferId());
            case PHONE -> phoneRepository.deleteByPhoneTransferId(cmd.getTransferId());
            case ACCOUNT -> accountRepository.deleteByAccountTransferId(cmd.getTransferId());
            default -> throw new IllegalArgumentException("Unsupported transfer type");
        }
    }

    private void handleUpsert(SuspiciousTransferCommand cmd) {
        if (cmd == null) {
            throw new IllegalArgumentException("Suspicious Transfer Command is required");
        }
        if (cmd.getTransferType() == null) {
            throw new IllegalArgumentException("Transfer Type is required");
        }
        if (cmd.getTransferId() == null) {
            throw new IllegalArgumentException("Transfer Id is required");
        }

        BigDecimal amount = cmd.getAmount();
        boolean blocked = false;
        boolean suspicious = false;
        String blockedReason = null;
        String suspiciousReason = null;

        if (amount != null) {
            if (amount.compareTo(BLOCK_THRESHOLD) > 0) {
                blocked = true;
                blockedReason = "Amount > " + BLOCK_THRESHOLD;
                suspiciousReason = "NOT_SUSPICIOUS";
            } else if (amount.compareTo(SUSPICIOUS_THRESHOLD) > 0) {
                suspicious = true;
                suspiciousReason = "Amount > " + SUSPICIOUS_THRESHOLD;
                blockedReason = "NOT_BLOCKED";
            } else {
                blockedReason = "NOT_BLOCKED";
                suspiciousReason = "NOT_SUSPICIOUS";
            }
        }
        switch (cmd.getTransferType()) {
            case CARD -> {
                SuspiciousCardTransferDto dto = new SuspiciousCardTransferDto();
                dto.setCardTransferId(cmd.getTransferId());
                dto.setIsBlocked(blocked);
                dto.setIsSuspicious(suspicious);
                dto.setBlockedReason(blockedReason);
                dto.setSuspiciousReason(suspiciousReason);
                upsertCard(dto);
            }
            case PHONE -> {
                SuspiciousPhoneTransferDto dto = new SuspiciousPhoneTransferDto();
                dto.setPhoneTransferId(cmd.getTransferId());
                dto.setIsBlocked(blocked);
                dto.setIsSuspicious(suspicious);
                dto.setBlockedReason(blockedReason);
                dto.setSuspiciousReason(suspiciousReason);
                upsertPhone(dto);
            }
            case ACCOUNT -> {
                SuspiciousAccountTransferDto dto = new SuspiciousAccountTransferDto();
                dto.setAccountTransferId(cmd.getTransferId());
                dto.setIsBlocked(blocked);
                dto.setIsSuspicious(suspicious);
                dto.setBlockedReason(blockedReason);
                dto.setSuspiciousReason(suspiciousReason);
                upsertAccount(dto);
            }
            default -> throw new IllegalArgumentException("Unsupported Transfer Type: " + cmd.getTransferType());
        }
    }
    public void handleGet(SuspiciousTransferCommand cmd) {
        if (cmd == null || cmd.getReplyTopic() == null || cmd.getCorrelationId() == null) {
            throw new IllegalArgumentException("GET cmd must contain replyTopic and correlationId");
        }

        List<SuspiciousTransferViewDto> result = new ArrayList<>();

        cardRepo.findAllByIsSuspiciousTrueOrIsBlockedTrue().forEach(e -> result.add(mapCard(e)));
        phoneRepo.findAllByIsSuspiciousTrueOrIsBlockedTrue().forEach(e -> result.add(mapPhone(e)));
        accountRepo.findAllByIsSuspiciousTrueOrIsBlockedTrue().forEach(e -> result.add(mapAccount(e)));

        SuspiciousTransfersGetResponse response = SuspiciousTransfersGetResponse.builder()
                .correlationId(cmd.getCorrelationId())
                .transfers(result)
                .build();

        kafkaTemplate.send(cmd.getReplyTopic(), cmd.getCorrelationId(), response);
    }

    private SuspiciousTransferViewDto mapCard(SuspiciousCardTransfer e) {
        return SuspiciousTransferViewDto.builder()
                .transferType(TransferType.CARD)
                .transferId(e.getCardTransferId())
                .isBlocked(e.getIsBlocked())
                .isSuspicious(e.getIsSuspicious())
                .blockedReason(e.getBlockedReason())
                .suspiciousReason(e.getSuspiciousReason())
                .build();
    }

    private SuspiciousTransferViewDto mapPhone(SuspiciousPhoneTransfer e) {
        return SuspiciousTransferViewDto.builder()
                .transferType(TransferType.PHONE)
                .transferId(e.getPhoneTransferId())
                .isBlocked(e.getIsBlocked())
                .isSuspicious(e.getIsSuspicious())
                .blockedReason(e.getBlockedReason())
                .suspiciousReason(e.getSuspiciousReason())
                .build();
    }

    private SuspiciousTransferViewDto mapAccount(SuspiciousAccountTransfer e) {
        return SuspiciousTransferViewDto.builder()
                .transferType(TransferType.ACCOUNT)
                .transferId(e.getAccountTransferId())
                .isBlocked(e.getIsBlocked())
                .isSuspicious(e.getIsSuspicious())
                .blockedReason(e.getBlockedReason())
                .suspiciousReason(e.getSuspiciousReason())
                .build();
    }
}