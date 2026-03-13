package com.bank.antifraud.aop;

import com.bank.antifraud.dto.AuditDto;
import com.bank.antifraud.enums.TransferType;
import com.bank.antifraud.kafka.dto.SuspiciousTransferCommand;
import com.bank.antifraud.repository.SuspiciousAccountTransferRepository;
import com.bank.antifraud.repository.SuspiciousCardTransferRepository;
import com.bank.antifraud.repository.SuspiciousPhoneTransferRepository;
import com.bank.antifraud.service.AuditService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.hibernate.sql.Update;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.Instant;

import static com.bank.antifraud.enums.OperationType.CREATE;
import static com.bank.antifraud.enums.OperationType.UPDATE;

@Aspect
@Component
@RequiredArgsConstructor
public class AuditAspect {

    private final AuditService auditService;
    private final ObjectMapper objectMapper;
    private final SuspiciousAccountTransferRepository accountRepository;
    private final SuspiciousCardTransferRepository cardRepository;
    private final SuspiciousPhoneTransferRepository phoneRepository;

    @Around("execution(* com.bank.antifraud.service.SuspiciousTransferServiceImpl.handleCreate(..)) && args(cmd)")
    public Object auditCreate(ProceedingJoinPoint joinPoint, SuspiciousTransferCommand cmd) throws Throwable {
        Object result = joinPoint.proceed();
        AuditDto auditDto = AuditDto.builder().entityType(TransferType.valueOf(cmd.getTransferType().name())).operationType(CREATE).createdBy(resolveCurrentUser()).createdAt(Instant.now()).entityJson("{}").newEntityJson(getCurrentEntityJson(cmd)).build();
        auditService.send(auditDto);
        return result;
    }

    @Around("execution(* com.bank.antifraud.service.SuspiciousTransferServiceImpl.handleUpdate(..)) && args(cmd)")
    public Object auditUpdate(ProceedingJoinPoint joinPoint, SuspiciousTransferCommand cmd) throws Throwable {
        String oldJson = getCurrentEntityJson(cmd);
        Object result = joinPoint.proceed();
        String newJson = getCurrentEntityJson(cmd);
        AuditDto auditDto = AuditDto.builder().entityType(TransferType.valueOf(cmd.getTransferType().name())).operationType(UPDATE).modifiedBy(resolveCurrentUser()).modifiedAt(Instant.now()).entityJson(oldJson).newEntityJson(newJson).build();
        auditService.send(auditDto);
        return result;
    }

    private String getCurrentEntityJson(SuspiciousTransferCommand cmd) {
        try {
            Object entity = switch (cmd.getTransferType()) {
                case CARD -> cardRepository.findByCardTransferId(cmd.getTransferId()).orElse(null);
                case PHONE -> phoneRepository.findByPhoneTransferId(cmd.getTransferId()).orElse(null);
                case ACCOUNT -> accountRepository.findByAccountTransferId(cmd.getTransferId()).orElse(null);
            };
            if (entity == null) {
                return "{}";
            }
            return objectMapper.writeValueAsString(entity);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize entity for audit", e);
        }
    }

    private String resolveCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            return "SYSTEM";
        }
        return authentication.getName();
    }

}
