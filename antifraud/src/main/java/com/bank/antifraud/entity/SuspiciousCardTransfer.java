package com.bank.antifraud.entity;

import jakarta.persistence.*;
import lombok.*;

@Builder
@Entity
@Table(name = "suspicious_card_transfer")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SuspiciousCardTransfer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "card_transfer_id", nullable = false, unique = true)
    private Long cardTransferId;

    @Column(name = "is_blocked")
    private Boolean isBlocked;

    @Column(name = "is_suspicious")
    private Boolean isSuspicious;

    @Column(name = "blocked_reason")
    private String blockedReason;

    @Column(name = "suspicious_reason")
    private String suspiciousReason;

    public SuspiciousCardTransfer(Long cardTransferId) {
    }

}
