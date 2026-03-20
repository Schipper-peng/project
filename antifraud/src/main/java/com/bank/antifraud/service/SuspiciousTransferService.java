package com.bank.antifraud.service;


import com.bank.antifraud.dto.suspicious.SuspiciousAccountTransferDto;
import com.bank.antifraud.dto.suspicious.SuspiciousCardTransferDto;
import com.bank.antifraud.dto.suspicious.SuspiciousPhoneTransferDto;

public interface SuspiciousTransferService {

    SuspiciousAccountTransferDto createAccount(SuspiciousAccountTransferDto dto);
    SuspiciousCardTransferDto createCard(SuspiciousCardTransferDto dto);
    SuspiciousPhoneTransferDto createPhone(SuspiciousPhoneTransferDto dto);

    SuspiciousAccountTransferDto updateAccount(SuspiciousAccountTransferDto dto);
    SuspiciousCardTransferDto updateCard(SuspiciousCardTransferDto dto);
    SuspiciousPhoneTransferDto updatePhone(SuspiciousPhoneTransferDto dto);

    void deleteAccount(Long id);
    void deleteCard(Long id);
    void deletePhone(Long id);

    SuspiciousAccountTransferDto getAccount(Long id);
    SuspiciousCardTransferDto getCard(Long id);
    SuspiciousPhoneTransferDto getPhone(Long id);

}
