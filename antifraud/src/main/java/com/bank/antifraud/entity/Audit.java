package com.bank.antifraud.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "audit")
public class Audit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "entity_type", nullable = false)
    private String entityType;

    @Column(name = "operation_type", nullable = false)
    private String operationType;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "modified_by")
    private String modifiedBy;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "modified_at")
    private Instant modifiedAt;

    @Lob
    @Column(name = "new_entity_json", nullable = false)
    private String newEntityJson;

    @Lob
    @Column(name = "entity_json")
    private String entityJson;

}
