package com.bank.antifraud.service;
import com.bank.antifraud.entity.Audit;
import com.bank.antifraud.enums.TransferType;

public interface AuditService {
     default void log(TransferType entityType, String operationType, String entityJson, String newEntityJson) {}
}
