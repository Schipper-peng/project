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
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import com.bank.antifraud.repository.SuspiciousAccountTransferRepository;
import com.bank.antifraud.repository.SuspiciousCardTransferRepository;
import com.bank.antifraud.repository.SuspiciousPhoneTransferRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class SuspiciousTransferServiceImpl implements SuspiciousTransferService {

    private final SuspiciousAccountTransferRepository accountRepository;
    private final SuspiciousCardTransferRepository cardRepository;
    private final SuspiciousPhoneTransferRepository phoneRepository;
    private final SuspiciousCardTransferMapper cardMapper;
    private final SuspiciousAccountTransferMapper accountMapper;
    private final SuspiciousPhoneTransferMapper phoneMapper;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final FraudAnalyzer fraudAnalyzer;

    public SuspiciousCardTransferDto upsertCard(SuspiciousCardTransferDto dto) {
        if (dto == null || dto.getCardTransferId() == null) {
            throw new IllegalArgumentException("Card transfer id is required");
        }

        log.info("Upserting CARD transfer: transferId={}", dto.getCardTransferId());

        SuspiciousCardTransfer entity = cardRepository.findByCardTransferId(dto.getCardTransferId()).map(existing -> {
            log.info("CARD transfer found, updating existing entity: transferId={}", dto.getCardTransferId());
            cardMapper.apply(dto, existing);
            return existing;
        }).orElseGet(() -> {
            log.info("CARD transfer not found, creating new entity: transferId={}", dto.getCardTransferId());
            return cardMapper.toEntity(dto);
        });

        SuspiciousCardTransfer saved = cardRepository.save(entity);

        log.info("CARD transfer saved: transferId={}, blocked={}, suspicious={}", saved.getCardTransferId(), saved.getIsBlocked(), saved.getIsSuspicious());

        return cardMapper.toDto(saved);
    }

    public SuspiciousPhoneTransferDto upsertPhone(SuspiciousPhoneTransferDto dto) {
        if (dto.getPhoneTransferId() == null) {
            throw new IllegalArgumentException("Phone Transfer Id is required");
        }

        log.info("Upserting PHONE transfer: transferId={}", dto.getPhoneTransferId());

        SuspiciousPhoneTransfer entity = phoneRepository.findByPhoneTransferId(dto.getPhoneTransferId()).map(existing -> {
            log.info("Phone transfer found, updating existing entity: transferId={}", dto.getPhoneTransferId());
            phoneMapper.apply(dto, existing);
            return existing;
        }).orElseGet(() -> {
            log.info("Phone transfer not found, creating new entity: transferId={}", dto.getPhoneTransferId());
            return phoneMapper.toEntity(dto);
        });

        SuspiciousPhoneTransfer saved = phoneRepository.save(entity);

        log.info("Phone transfer saved: transferId={}, blocked={}, suspicious={}", saved.getPhoneTransferId(), saved.getIsBlocked(), saved.getIsSuspicious());

        return phoneMapper.toDto(saved);
    }

    public SuspiciousAccountTransferDto upsertAccount(SuspiciousAccountTransferDto dto) {
        if (dto.getAccountTransferId() == null) {
            throw new IllegalArgumentException("Account Transfer Id is required");
        }

        log.info("Upserting ACCOUNT transfer: transferId={}", dto.getAccountTransferId());

        SuspiciousAccountTransfer entity = accountRepository.findByAccountTransferId(dto.getAccountTransferId()).map(existing -> {
            log.info("ACCOUNT transfer found, updating existing entity: transferId={}", dto.getAccountTransferId());
            accountMapper.apply(dto, existing);
            return existing;
        }).orElseGet(() -> {
            log.info("ACCOUNT transfer not found, creating new entity: transferId={}", dto.getAccountTransferId());
            return accountMapper.toEntity(dto);
        });

        SuspiciousAccountTransfer saved = accountRepository.save(entity);

        log.info("ACCOUNT transfer saved: transferId={}, blocked={}, suspicious={}", saved.getAccountTransferId(), saved.getIsBlocked(), saved.getIsSuspicious());

        return accountMapper.toDto(saved);
    }

    @Override
    public void handleCreate(SuspiciousTransferCommand cmd) {
        handleUpsert(cmd);
    }

    @Override
    public void handleUpdate(SuspiciousTransferCommand cmd) {
        log.info("Handling UPDATE command: transferType={}, transferId={}, amount={}", cmd != null ? cmd.getTransferType() : null, cmd != null ? cmd.getTransferId() : null, cmd != null ? cmd.getAmount() : null);

        handleUpsert(cmd);
    }

    @Override
    public void handleDelete(SuspiciousTransferCommand cmd) {
        log.info("Handling DELETE command: transferType={}, transferId={}", cmd != null ? cmd.getTransferType() : null, cmd != null ? cmd.getTransferId() : null);

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

        log.info("Validated UPSERT command: transferType={}, transferId={}, amount={}", cmd.getTransferType(), cmd.getTransferId(), cmd.getAmount());

        FraudDecision decision = fraudAnalyzer.analyze(cmd.getAmount());

        log.info("Fraud decision calculated: transferType={}, transferId={}, blocked={}, suspicious={}, blockedReason={}, suspiciousReason={}", cmd.getTransferType(), cmd.getTransferId(), decision.isBlocked(), decision.isSuspicious(), decision.getBlockedReason(), decision.getSuspiciousReason());

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
        }
        if (cmd.getTransferType() == null) {
            throw new IllegalArgumentException("Transfer Type is required");
        }
        if (cmd.getTransferId() == null) {
            throw new IllegalArgumentException("Transfer Id is required");
        }
        if (cmd.getAmount() == null) {
            throw new IllegalArgumentException("Amount is required");
        }
    }

    private void validateDeleteCommand(SuspiciousTransferCommand cmd) {
        if (cmd == null) {
            throw new IllegalArgumentException("Delete Command is required");
        }
        if (cmd.getTransferType() == null) {
            throw new IllegalArgumentException("Transfer Type is required");
        }
        if (cmd.getTransferId() == null) {
            throw new IllegalArgumentException("Transfer Id is required");
        }
    }

    private void validateGetQuery(SuspiciousTransferQuery query) {
        if (query == null) {
            throw new IllegalArgumentException("GET query is required");
        }
        if (query.getReplyTopic() == null || query.getReplyTopic().isBlank()) {
            throw new IllegalArgumentException("Reply topic is required");
        }
        if (query.getCorrelationId() == null || query.getCorrelationId().isBlank()) {
            throw new IllegalArgumentException("Correlation id is required");
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
        log.info("Handling GET query: correlationId={}, replyTopic={}", query != null ? query.getCorrelationId() : null, query != null ? query.getReplyTopic() : null);

        validateGetQuery(query);

        List<SuspiciousTransferViewDto> result = new ArrayList<>();

        cardRepository.findAllByIsSuspiciousTrueOrIsBlockedTrue().forEach(e -> result.add(mapCard(e)));
        phoneRepository.findAllByIsSuspiciousTrueOrIsBlockedTrue().forEach(e -> result.add(mapPhone(e)));
        accountRepository.findAllByIsSuspiciousTrueOrIsBlockedTrue().forEach(e -> result.add(mapAccount(e)));

        SuspiciousTransferResponse response = SuspiciousTransferResponse.builder().correlationId(query.getCorrelationId()).transfers(result).build();

        kafkaTemplate.send(query.getReplyTopic(), query.getCorrelationId(), response);
    }

    private SuspiciousTransferViewDto mapCard(SuspiciousCardTransfer e) {
        return SuspiciousTransferViewDto.builder().transferType(TransferType.CARD).transferId(e.getCardTransferId()).isBlocked(e.getIsBlocked()).isSuspicious(e.getIsSuspicious()).blockedReason(e.getBlockedReason()).suspiciousReason(e.getSuspiciousReason()).build();
    }

    private SuspiciousTransferViewDto mapPhone(SuspiciousPhoneTransfer e) {
        return SuspiciousTransferViewDto.builder().transferType(TransferType.PHONE).transferId(e.getPhoneTransferId()).isBlocked(e.getIsBlocked()).isSuspicious(e.getIsSuspicious()).blockedReason(e.getBlockedReason()).suspiciousReason(e.getSuspiciousReason()).build();
    }

    private SuspiciousTransferViewDto mapAccount(SuspiciousAccountTransfer e) {
        return SuspiciousTransferViewDto.builder().transferType(TransferType.ACCOUNT).transferId(e.getAccountTransferId()).isBlocked(e.getIsBlocked()).isSuspicious(e.getIsSuspicious()).blockedReason(e.getBlockedReason()).suspiciousReason(e.getSuspiciousReason()).build();
    }

}