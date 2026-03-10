package com.bank.antifraud.dto;

import lombok.*;

import java.time.Instant;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditDto {
    private String entityType;
    private String operationType;
    private String createdBy;
    private String modifiedBy;
    private Instant createdAt;
    private Instant modifiedAt;
    private String newEntityJson;
    private String entityJson;
}
