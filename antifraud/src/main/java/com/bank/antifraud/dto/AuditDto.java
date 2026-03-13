package com.bank.antifraud.dto;

import com.bank.antifraud.enums.OperationType;
import com.bank.antifraud.enums.TransferType;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class AuditDto {

    private TransferType entityType;

    private OperationType operationType;

    private String createdBy;

    private String modifiedBy;

    private Instant createdAt;

    private Instant modifiedAt;

    private String newEntityJson;

    private String entityJson;

}
