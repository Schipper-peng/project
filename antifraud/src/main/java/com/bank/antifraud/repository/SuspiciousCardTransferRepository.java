package com.bank.antifraud.repository;

import com.bank.antifraud.entity.SuspiciousCardTransfer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SuspiciousCardTransferRepository extends JpaRepository<SuspiciousCardTransfer, Long> {
    Optional<SuspiciousCardTransfer> findByCardTransferId(Long cardTransferId);
    void  deleteByCardTransferId(Long cardTransferId);
    List<SuspiciousCardTransfer> findAllByIsSuspiciousTrueOrIsBlockedTrue();
}
