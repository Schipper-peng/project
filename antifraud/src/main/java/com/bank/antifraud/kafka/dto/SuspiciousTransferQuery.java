package com.bank.antifraud.kafka.dto;

import com.bank.antifraud.enums.OperationType;
import com.bank.antifraud.enums.TransferType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SuspiciousTransferQuery {
    private OperationType operationType;
    private TransferType transferType;
    private Long transferId;
    private BigDecimal amount;
}
