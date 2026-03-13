package com.bank.antifraud.kafka.dto;

import com.bank.antifraud.enums.OperationType;
import com.bank.antifraud.enums.TransferType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SuspiciousTransferQuery {

    @NotBlank
    private String correlationId;

    @NotBlank
    private String replyTopic;

}
