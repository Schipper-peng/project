package com.bank.antifraud.exception;

public class InvalidSuspiciousTransferCommandException extends RuntimeException {

    public InvalidSuspiciousTransferCommandException(String message) {
        super(message);
    }

}
