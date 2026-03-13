package com.bank.antifraud.repository;

import com.bank.antifraud.entity.SuspiciousAccountTransfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SuspiciousAccountTransferRepository extends JpaRepository<SuspiciousAccountTransfer, Long> {

    Optional<SuspiciousAccountTransfer> findByAccountTransferId(Long accountTransferId);

    void deleteByAccountTransferId(Long accountTransferId);

    List<SuspiciousAccountTransfer> findAllByIsSuspiciousTrueOrIsBlockedTrue();

}
