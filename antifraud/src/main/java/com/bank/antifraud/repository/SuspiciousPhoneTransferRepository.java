package com.bank.antifraud.repository;

import com.bank.antifraud.entity.SuspiciousPhoneTransfer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SuspiciousPhoneTransferRepository extends JpaRepository<SuspiciousPhoneTransfer, Long> {
    Optional<SuspiciousPhoneTransfer> findByPhoneTransferId(Long phoneTransferId);
    void deleteByPhoneTransferId(Long phoneTransferId);
    List<SuspiciousPhoneTransfer> findAllByIsSuspiciousTrueOrIsBlockedTrue();
}
