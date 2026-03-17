package com.bank.antifraud.kafka.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

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
