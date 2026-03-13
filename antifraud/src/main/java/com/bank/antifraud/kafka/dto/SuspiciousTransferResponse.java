package com.bank.antifraud.kafka.dto;

import com.bank.antifraud.enums.TransferType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.kafka.common.protocol.types.Field;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SuspiciousTransferResponse {

    private String correlationId;
    private List<SuspiciousTransferViewDto> transfers;

}
