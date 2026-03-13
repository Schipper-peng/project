package com.bank.antifraud.kafka.dto;

import com.bank.antifraud.enums.OperationType;
import com.bank.antifraud.enums.TransferType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SuspiciousTransferCommand {

    @NotNull
    private OperationType operationType;

    @NotNull
    private TransferType transferType;

    @NotNull
    @Positive
    private Long transferId;

    @PositiveOrZero
    private BigDecimal amount;

}
