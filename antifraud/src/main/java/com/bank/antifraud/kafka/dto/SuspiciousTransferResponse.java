package com.bank.antifraud.kafka.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SuspiciousTransferResponse {

    private String correlationId;
    private List<SuspiciousTransferViewDto> transfers;

}
