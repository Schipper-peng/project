package com.bank.antifraud.kafka;

public class KafkaTopics {
    public KafkaTopics() {}
    public static final String CREATE = "suspicious-transfers.create";
    public static final String UPDATE = "suspicious-transfers.update";
    public static final String DELETE = "suspicious-transfers.delete";
    public static final String GET = "suspicious-transfers.get";

}
