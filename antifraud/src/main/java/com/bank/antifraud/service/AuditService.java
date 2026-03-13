package com.bank.antifraud.service;

import com.bank.antifraud.dto.AuditDto;
import com.bank.antifraud.entity.Audit;
import com.bank.antifraud.enums.TransferType;

public interface AuditService {

    void send(AuditDto auditDto);

}
