package com.bank.antifraud.kafka;

public class KafkaTopics {
    public KafkaTopics() {
    }

    public static final String CREATE = "suspicious-transfers.create";
    public static final String UPDATE = "suspicious-transfers.update";
    public static final String DELETE = "suspicious-transfers.delete";
    public static final String GET = "suspicious-transfers.get";
    public static final String GET_REPLY = "suspicious-transfers.get.reply";
    public static final String AUDIT = "audit-events";

    public static final String ACCOUNT_TRANSFER = "transfer.antifraud.account";
    public static final String CARD_TRANSFER = "transfer.antifraud.card";
    public static final String PHONE_TRANSFER = "transfer.antifraud.phone";

    public static final String FRAUD_DECISION = "transfer.fraud.decision";

}
