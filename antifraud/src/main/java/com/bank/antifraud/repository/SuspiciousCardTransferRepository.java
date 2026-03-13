package com.bank.antifraud.repository;

import com.bank.antifraud.entity.SuspiciousCardTransfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SuspiciousCardTransferRepository extends JpaRepository<SuspiciousCardTransfer, Long> {

    Optional<SuspiciousCardTransfer> findByCardTransferId(Long cardTransferId);

    void deleteByCardTransferId(Long cardTransferId);

    List<SuspiciousCardTransfer> findAllByIsSuspiciousTrueOrIsBlockedTrue();

}
