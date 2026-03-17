package com.bank.antifraud.aop;

import com.bank.antifraud.dto.AuditDto;
import com.bank.antifraud.entity.SuspiciousAccountTransfer;
import com.bank.antifraud.entity.SuspiciousCardTransfer;
import com.bank.antifraud.entity.SuspiciousPhoneTransfer;
import com.bank.antifraud.kafka.dto.SuspiciousTransferCommand;
import com.bank.antifraud.kafka.producer.AuditProducer;
import com.bank.antifraud.repository.SuspiciousAccountTransferRepository;
import com.bank.antifraud.repository.SuspiciousCardTransferRepository;
import com.bank.antifraud.repository.SuspiciousPhoneTransferRepository;
import com.bank.antifraud.service.AuditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;


@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class AuditAspect {

    private static final String CORRELATION_ID = "correlationId";
    private static final String SYSTEM_USER = "SYSTEM";

    private final AuditService auditService;
    private final AuditProducer auditProducer;
    private final SuspiciousAccountTransferRepository accountRepository;
    private final SuspiciousCardTransferRepository cardRepository;
    private final SuspiciousPhoneTransferRepository phoneRepository;

    @Around("execution(* com.bank.antifraud.service.SuspiciousTransferServiceImpl.handleCreate(..)) && args(cmd)")
    public Object auditCreate(ProceedingJoinPoint joinPoint, SuspiciousTransferCommand cmd) throws Throwable {
        Object result = joinPoint.proceed();

        Object created = findEntity(cmd);
        if (created != null) {
            AuditDto auditDto = auditService.fromCreate(
                    cmd.getTransferType(),
                    created,
                    resolveCurrentUser()
            );

            String corrId = corrId();
            auditProducer.sendAudit(String.valueOf(cmd.getTransferId()), auditDto, corrId);

            log.info("AUDIT CREATE sent. transferType={}, transferId={}, corrId={}",
                    cmd.getTransferType(), cmd.getTransferId(), corrId);
        } else {
            log.warn("AUDIT CREATE skipped: entity not found after create. transferType={}, transferId={}",
                    cmd.getTransferType(), cmd.getTransferId());
        }

        return result;
    }

    @Around("execution(* com.bank.antifraud.service.SuspiciousTransferServiceImpl.handleUpdate(..)) && args(cmd)")
    public Object auditUpdate(ProceedingJoinPoint joinPoint, SuspiciousTransferCommand cmd) throws Throwable {
        Object before = findEntity(cmd);
        Object beforeSnap = before == null ? null : snapshot(cmd, before);

        Object result = joinPoint.proceed();

        Object after = findEntity(cmd);
        if (beforeSnap != null && after != null) {
            AuditDto auditDto = auditService.fromUpdate(
                    cmd.getTransferType(),
                    beforeSnap,
                    after,
                    resolveCurrentUser()
            );

            String corrId = corrId();
            auditProducer.sendAudit(String.valueOf(cmd.getTransferId()), auditDto, corrId);

            log.info("AUDIT UPDATE sent. transferType={}, transferId={}, corrId={}",
                    cmd.getTransferType(), cmd.getTransferId(), corrId);
        } else {
            log.warn("AUDIT UPDATE skipped. beforeSnap={}, after={}, transferType={}, transferId={}",
                    beforeSnap != null, after != null, cmd.getTransferType(), cmd.getTransferId());
        }

        return result;
    }


    private Object findEntity(SuspiciousTransferCommand cmd) {
        return switch (cmd.getTransferType()) {
            case CARD -> cardRepository.findByCardTransferId(cmd.getTransferId()).orElse(null);
            case PHONE -> phoneRepository.findByPhoneTransferId(cmd.getTransferId()).orElse(null);
            case ACCOUNT -> accountRepository.findByAccountTransferId(cmd.getTransferId()).orElse(null);
        };
    }

    private Object snapshot(SuspiciousTransferCommand cmd, Object entity) {
        return switch (cmd.getTransferType()) {
            case CARD -> snapshotCard((SuspiciousCardTransfer) entity);
            case PHONE -> snapshotPhone((SuspiciousPhoneTransfer) entity);
            case ACCOUNT -> snapshotAccount((SuspiciousAccountTransfer) entity);
        };
    }

    private SuspiciousCardTransfer snapshotCard(SuspiciousCardTransfer entity) {
        return SuspiciousCardTransfer.builder()
                .id(entity.getId())
                .cardTransferId(entity.getCardTransferId())
                .isBlocked(entity.getIsBlocked())
                .isSuspicious(entity.getIsSuspicious())
                .blockedReason(entity.getBlockedReason())
                .suspiciousReason(entity.getSuspiciousReason())
                .build();
    }

    private SuspiciousPhoneTransfer snapshotPhone(SuspiciousPhoneTransfer entity) {
        return SuspiciousPhoneTransfer.builder()
                .id(entity.getId())
                .phoneTransferId(entity.getPhoneTransferId())
                .isBlocked(entity.getIsBlocked())
                .isSuspicious(entity.getIsSuspicious())
                .blockedReason(entity.getBlockedReason())
                .suspiciousReason(entity.getSuspiciousReason())
                .build();
    }

    private SuspiciousAccountTransfer snapshotAccount(SuspiciousAccountTransfer entity) {
        return SuspiciousAccountTransfer.builder()
                .id(entity.getId())
                .accountTransferId(entity.getAccountTransferId())
                .isBlocked(entity.getIsBlocked())
                .isSuspicious(entity.getIsSuspicious())
                .blockedReason(entity.getBlockedReason())
                .suspiciousReason(entity.getSuspiciousReason())
                .build();
    }

    private String resolveCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null || authentication.getName().isBlank()) {
            return SYSTEM_USER;
        }
        return authentication.getName();
    }

    private String corrId() {
        String fromMdc = MDC.get(CORRELATION_ID);
        return (fromMdc == null || fromMdc.isBlank())
                ? UUID.randomUUID().toString()
                : fromMdc;
    }

}
