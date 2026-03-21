package com.bank.antifraud.service.impl;

import com.bank.antifraud.aop.Auditable;
import com.bank.antifraud.dto.suspicious.SuspiciousAccountTransferDto;
import com.bank.antifraud.dto.suspicious.SuspiciousCardTransferDto;
import com.bank.antifraud.dto.suspicious.SuspiciousPhoneTransferDto;
import com.bank.antifraud.entity.SuspiciousAccountTransfer;
import com.bank.antifraud.entity.SuspiciousCardTransfer;
import com.bank.antifraud.entity.SuspiciousPhoneTransfer;
import com.bank.antifraud.enums.OperationType;
import com.bank.antifraud.kafka.producer.SuspiciousTransferProducer;
import com.bank.antifraud.mappers.SuspiciousAccountTransferMapper;
import com.bank.antifraud.mappers.SuspiciousCardTransferMapper;
import com.bank.antifraud.mappers.SuspiciousPhoneTransferMapper;
import com.bank.antifraud.service.SuspiciousTransferService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.bank.antifraud.repository.SuspiciousAccountTransferRepository;
import com.bank.antifraud.repository.SuspiciousCardTransferRepository;
import com.bank.antifraud.repository.SuspiciousPhoneTransferRepository;
import org.springframework.transaction.annotation.Transactional;


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


    @Override
    @Auditable(OperationType.CREATE)
    public SuspiciousAccountTransferDto createAccount(SuspiciousAccountTransferDto dto) {
        SuspiciousAccountTransfer saved = accountRepository.save(accountMapper.toEntity(dto));
        return accountMapper.toDto(saved);
    }

    @Override
    @Auditable(OperationType.CREATE)
    public SuspiciousCardTransferDto createCard(SuspiciousCardTransferDto dto) {
        SuspiciousCardTransfer saved = cardRepository.save(cardMapper.toEntity(dto));
        return cardMapper.toDto(saved);
    }

    @Override
    @Auditable(OperationType.CREATE)
    public SuspiciousPhoneTransferDto createPhone(SuspiciousPhoneTransferDto dto) {
        SuspiciousPhoneTransfer saved = phoneRepository.save(phoneMapper.toEntity(dto));
        return phoneMapper.toDto(saved);
    }

    @Override
    @Auditable(OperationType.UPDATE)
    public SuspiciousAccountTransferDto updateAccount(SuspiciousAccountTransferDto dto) {
        SuspiciousAccountTransfer entity = accountRepository.findById(dto.getId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "SuspiciousAccountTransfer not found: " + dto.getId()));

        accountMapper.updateEntityFromDto(dto, entity);
        return accountMapper.toDto(accountRepository.save(entity));
    }

    @Override
    @Auditable(OperationType.UPDATE)
    public SuspiciousCardTransferDto updateCard(SuspiciousCardTransferDto dto) {
        SuspiciousCardTransfer entity = cardRepository.findById(dto.getId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "SuspiciousCardTransfer not found: " + dto.getId()));

        cardMapper.updateEntityFromDto(dto, entity);
        return cardMapper.toDto(cardRepository.save(entity));
    }

    @Override
    @Auditable(OperationType.UPDATE)
    public SuspiciousPhoneTransferDto updatePhone(SuspiciousPhoneTransferDto dto) {
        SuspiciousPhoneTransfer entity = phoneRepository.findById(dto.getId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "SuspiciousPhoneTransfer not found: " + dto.getId()));

        phoneMapper.updateEntityFromDto(dto, entity);
        return phoneMapper.toDto(phoneRepository.save(entity));
    }

    @Override
    public void deleteAccount(Long id) {
        if (!accountRepository.existsById(id)) {
            throw new EntityNotFoundException("SuspiciousAccountTransfer not found: " + id);
        }
        accountRepository.deleteById(id);
    }

    @Override
    public void deleteCard(Long id) {
        if (!cardRepository.existsById(id)) {
            throw new EntityNotFoundException("SuspiciousCardTransfer not found: " + id);
        }
        cardRepository.deleteById(id);
    }

    @Override
    public void deletePhone(Long id) {
        if (!phoneRepository.existsById(id)) {
            throw new EntityNotFoundException("SuspiciousPhoneTransfer not found: " + id);
        }
        phoneRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public SuspiciousAccountTransferDto getAccount(Long id) {
        return accountMapper.toDto(accountRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "SuspiciousAccountTransfer not found: " + id)));
    }

    @Override
    @Transactional(readOnly = true)
    public SuspiciousCardTransferDto getCard(Long id) {
        return cardMapper.toDto(cardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "SuspiciousCardTransfer not found: " + id)));
    }

    @Override
    @Transactional(readOnly = true)
    public SuspiciousPhoneTransferDto getPhone(Long id) {
        return phoneMapper.toDto(phoneRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "SuspiciousPhoneTransfer not found: " + id)));
    }
}