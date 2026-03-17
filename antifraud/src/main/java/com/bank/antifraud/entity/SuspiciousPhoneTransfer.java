package com.bank.antifraud.entity;

import jakarta.persistence.*;
import lombok.*;

@Builder
@Entity
@Table(name = "suspicious_phone_transfers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SuspiciousPhoneTransfer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "phone_transfer_id", nullable = false, unique = true)
    private Long phoneTransferId;

    @Column(name = "is_blocked")
    private Boolean isBlocked;

    @Column(name = "is_suspicious")
    private Boolean isSuspicious;

    @Column(name = "blocked_reason")
    private String blockedReason;

    @Column(name = "suspicious_reason")
    private String suspiciousReason;

    public SuspiciousPhoneTransfer(Long phoneTransferId) {
    }

}
