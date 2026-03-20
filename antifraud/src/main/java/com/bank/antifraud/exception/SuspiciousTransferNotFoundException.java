package com.bank.antifraud.exception;

public class SuspiciousTransferNotFoundException extends RuntimeException {

    public SuspiciousTransferNotFoundException(String message) {
        super(message);
    }

}
