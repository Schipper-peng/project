package com.bank.antifraud.service;

import com.bank.antifraud.dto.SuspiciousAccountTransferDto;
import com.bank.antifraud.dto.SuspiciousCardTransferDto;
import com.bank.antifraud.dto.SuspiciousPhoneTransferDto;
import com.bank.antifraud.entity.SuspiciousAccountTransfer;
import com.bank.antifraud.entity.SuspiciousCardTransfer;
import com.bank.antifraud.entity.SuspiciousPhoneTransfer;
import com.bank.antifraud.enums.TransferType;
import com.bank.antifraud.kafka.dto.SuspiciousTransferCommand;
import com.bank.antifraud.kafka.dto.SuspiciousTransferQuery;
import com.bank.antifraud.kafka.dto.SuspiciousTransferResponse;
import com.bank.antifraud.kafka.dto.SuspiciousTransferViewDto;
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
import java.util.List;


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
    private final FraudAnalyzer fraudAnalyzer;


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
        validateDeleteCommand(cmd);
        switch (cmd.getTransferType()) {
            case CARD -> cardRepository.deleteByCardTransferId(cmd.getTransferId());
            case PHONE -> phoneRepository.deleteByPhoneTransferId(cmd.getTransferId());
            case ACCOUNT -> accountRepository.deleteByAccountTransferId(cmd.getTransferId());
            default -> throw new IllegalArgumentException("Unsupported transfer type" + cmd.getTransferType());
        }
    }

    private void handleUpsert(SuspiciousTransferCommand cmd) {
        validateUpsertCommand(cmd);

        FraudDecision decision = fraudAnalyzer.analyze(cmd.getAmount());

        switch (cmd.getTransferType()) {
            case CARD -> upsertCard(buildCardDto(cmd, decision));
            case PHONE -> upsertPhone(buildPhoneDto(cmd, decision));
            case ACCOUNT -> upsertAccount(buildAccountDto(cmd, decision));
            default -> throw new IllegalArgumentException("Unsupported transfer type" + cmd.getTransferType());
        }
    }
    private void validateUpsertCommand(SuspiciousTransferCommand cmd) {
        if (cmd == null) {
            throw new IllegalArgumentException("Suspicious Transfer Command is required");
        } if (cmd.getTransferType() == null) {
            throw new IllegalArgumentException("Transfer Type is required");
        }
        if (cmd.getTransferId() == null) {
            throw new IllegalArgumentException("Transfer Id is required");
        } if (cmd.getAmount() == null) {
            throw new IllegalArgumentException("Amount is required");
        }
    }
    private void validateDeleteCommand(SuspiciousTransferCommand cmd) {
        if (cmd == null) {
            throw new IllegalArgumentException("Delete Command is required");
        } if (cmd.getTransferType() == null) {
            throw new IllegalArgumentException("Transfer Type is required");
        } if (cmd.getTransferId() == null) {
            throw new IllegalArgumentException("Transfer Id is required");
        }
    }
    private SuspiciousCardTransferDto buildCardDto(SuspiciousTransferCommand cmd, FraudDecision decision) {
        SuspiciousCardTransferDto dto = new SuspiciousCardTransferDto();
        dto.setCardTransferId(cmd.getTransferId());
        dto.setIsBlocked(decision.isBlocked());
        dto.setIsSuspicious(decision.isSuspicious());
        dto.setBlockedReason(decision.getBlockedReason().name());
        dto.setSuspiciousReason(decision.getSuspiciousReason().name());
        return dto;
    }

    private SuspiciousPhoneTransferDto buildPhoneDto(SuspiciousTransferCommand cmd, FraudDecision decision) {
        SuspiciousPhoneTransferDto dto = new SuspiciousPhoneTransferDto();
        dto.setPhoneTransferId(cmd.getTransferId());
        dto.setIsBlocked(decision.isBlocked());
        dto.setIsSuspicious(decision.isSuspicious());
        dto.setBlockedReason(decision.getBlockedReason().name());
        dto.setSuspiciousReason(decision.getSuspiciousReason().name());
        return dto;
    }

    private SuspiciousAccountTransferDto buildAccountDto(SuspiciousTransferCommand cmd, FraudDecision decision) {
        SuspiciousAccountTransferDto dto = new SuspiciousAccountTransferDto();
        dto.setAccountTransferId(cmd.getTransferId());
        dto.setIsBlocked(decision.isBlocked());
        dto.setIsSuspicious(decision.isSuspicious());
        dto.setBlockedReason(decision.getBlockedReason().name());
        dto.setSuspiciousReason(decision.getSuspiciousReason().name());
        return dto;
    }

    public void handleGet(SuspiciousTransferQuery query) {
        if (query == null || query.getReplyTopic() == null || query.getCorrelationId() == null) {
            throw new IllegalArgumentException("GET query must contain replyTopic and correlationId");
        }

        List<SuspiciousTransferViewDto> result = new ArrayList<>();

        cardRepository.findAllByIsSuspiciousTrueOrIsBlockedTrue().forEach(e -> result.add(mapCard(e)));
        phoneRepository.findAllByIsSuspiciousTrueOrIsBlockedTrue().forEach(e -> result.add(mapPhone(e)));
        accountRepository.findAllByIsSuspiciousTrueOrIsBlockedTrue().forEach(e -> result.add(mapAccount(e)));

        SuspiciousTransferResponse response = SuspiciousTransferResponse.builder()
                .correlationId(query.getCorrelationId())
                .transfers(result)
                .build();

        kafkaTemplate.send(query.getReplyTopic(), query.getCorrelationId(), response);
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