package com.bank.antifraud.kafka.exception;

public class InvalidSuspiciousTransferCommandException extends RuntimeException {

    public InvalidSuspiciousTransferCommandException(String message) {
        super(message);
    }

}
