package com.bank.antifraud.service;

import com.bank.antifraud.dto.AuditDto;
import com.bank.antifraud.enums.TransferType;

public interface AuditService {


    AuditDto fromCreate(TransferType transferType, Object createdEntity, String createdBy);

    AuditDto fromUpdate(TransferType transferType, Object before, Object after, String modifiedBy);

}
