package com.bank.antifraud.auditLogging;

import com.bank.antifraud.enums.OperationType;
import com.bank.antifraud.repository.SuspiciousAccountTransferRepository;
import com.bank.antifraud.repository.SuspiciousCardTransferRepository;
import com.bank.antifraud.repository.SuspiciousPhoneTransferRepository;
import com.bank.antifraud.service.AuditService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import static com.bank.antifraud.enums.OperationType.UPDATE;

@Aspect
@Component
@RequiredArgsConstructor
public class AuditAspect {

//    private final AuditService auditService;
//    private final ObjectMapper objectMapper;
//
//    private final SuspiciousAccountTransferRepository accountRepo;
//    private final SuspiciousCardTransferRepository cardRepo;
//    private final SuspiciousPhoneTransferRepository phoneRepo;
//
//    @Around("@annotation(auditLog) && args(dto)")
//    public Object audit(ProceedingJoinPoint joinPoint, AuditLog auditLog, Object dto) throws Throwable {
//        String entityType = auditLog.entityType();
//
//        String oldJson = null;
//        String newJson = null;
//        String operationType = String.valueOf(UPDATE);
//
//        Object oldEntity = findExisting(entityType, dto);
//        if (oldEntity != null) {
//            operationType = String.valueOf(OperationType.CREATE);
//        } else {
//            oldJson = toJson(oldEntity);
//        }
    }
//}
