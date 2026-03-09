package com.bank.antifraud.service;

import com.bank.antifraud.dto.SuspiciousAccountTransferDto;
import com.bank.antifraud.dto.SuspiciousCardTransferDto;
import com.bank.antifraud.dto.SuspiciousPhoneTransferDto;
import com.bank.antifraud.kafka.dto.SuspiciousTransferCommand;

public interface SuspiciousTransferService {
    void handleCreate(SuspiciousTransferCommand cmd);
    void handleUpdate(SuspiciousTransferCommand cmd);
    void handleDelete(SuspiciousTransferCommand cmd);
    void handleGet(SuspiciousTransferCommand cmd);
}
