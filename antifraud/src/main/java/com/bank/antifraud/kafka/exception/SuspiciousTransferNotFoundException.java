package com.bank.antifraud.kafka.exception;

public class SuspiciousTransferNotFoundException extends RuntimeException {

    public SuspiciousTransferNotFoundException(String message) {
        super(message);
    }

}
