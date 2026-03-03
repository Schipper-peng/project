package com.bank.antifraud.repository;

import com.bank.antifraud.entity.SuspiciousAccountTransfer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SuspiciousAccountTransferRepository extends JpaRepository<SuspiciousAccountTransfer, Long> {
    Optional<SuspiciousAccountTransfer> findByAccountTransferId(Long accountTransferId);
    void deleteByAccountTransferId(Long accountTransferId);
    List<SuspiciousAccountTransfer> findAllByIsSuspiciousTrueOrIsBlockedTrue();
}
