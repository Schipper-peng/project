package com.bank.antifraud.aop;

import com.bank.antifraud.dto.audit.AuditDto;
import com.bank.antifraud.dto.suspicious.SuspiciousAccountTransferDto;
import com.bank.antifraud.dto.suspicious.SuspiciousCardTransferDto;
import com.bank.antifraud.dto.suspicious.SuspiciousPhoneTransferDto;
import com.bank.antifraud.enums.OperationType;
import com.bank.antifraud.kafka.producer.AuditProducer;
import com.bank.antifraud.mappers.SuspiciousAccountTransferMapper;
import com.bank.antifraud.mappers.SuspiciousCardTransferMapper;
import com.bank.antifraud.mappers.SuspiciousPhoneTransferMapper;
import com.bank.antifraud.repository.SuspiciousAccountTransferRepository;
import com.bank.antifraud.repository.SuspiciousCardTransferRepository;
import com.bank.antifraud.repository.SuspiciousPhoneTransferRepository;
import com.bank.antifraud.service.AuditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.UUID;


@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class AuditAspect {
    private final AuditService auditService;
    private final AuditProducer auditProducer;

    private final SuspiciousAccountTransferRepository accountRepository;
    private final SuspiciousCardTransferRepository cardRepository;
    private final SuspiciousPhoneTransferRepository phoneRepository;

    private final SuspiciousAccountTransferMapper accountMapper;
    private final SuspiciousCardTransferMapper cardMapper;
    private final SuspiciousPhoneTransferMapper phoneMapper;

    @Around("@annotation(auditable)")
    public Object around(ProceedingJoinPoint joinPoint, Auditable auditable) throws Throwable {
        OperationType operationType = auditable.value();

        if (operationType != OperationType.CREATE && operationType != OperationType.UPDATE) {
            return joinPoint.proceed();
        }

        Object before = operationType == OperationType.UPDATE
                ? extractBeforeForUpdate(joinPoint.getArgs()[0])
                : null;

        Object result = joinPoint.proceed();

        AuditDto auditDto = auditService.buildAudit(before, result, operationType);
        String correlationId = UUID.randomUUID().toString();

        auditProducer.sendAudit(resolveKey(before, result), auditDto, correlationId);

        log.info("Audit event sent: operationType={}, correlationId={}", operationType, correlationId);
        return result;
    }

    private Object extractBeforeForUpdate(Object arg) {
        if (arg instanceof SuspiciousAccountTransferDto dto && dto.getId() != null) {
            return accountRepository.findById(dto.getId())
                    .map(accountMapper::toDto)
                    .orElse(null);
        }
        if (arg instanceof SuspiciousCardTransferDto dto && dto.getId() != null) {
            return cardRepository.findById(dto.getId())
                    .map(cardMapper::toDto)
                    .orElse(null);
        }
        if (arg instanceof SuspiciousPhoneTransferDto dto && dto.getId() != null) {
            return phoneRepository.findById(dto.getId())
                    .map(phoneMapper::toDto)
                    .orElse(null);
        }
        return null;
    }

    private String resolveKey(Object before, Object after) {
        Object target = after != null ? after : before;
        return target != null ? target.getClass().getSimpleName() : "audit";
    }

}
