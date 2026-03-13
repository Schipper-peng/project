package com.bank.antifraud.kafka.producer;


import com.bank.antifraud.kafka.KafkaTopics;
import com.bank.antifraud.kafka.dto.SuspiciousTransferCommand;
import com.bank.antifraud.kafka.dto.SuspiciousTransferQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SuspiciousTransferProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void send(SuspiciousTransferCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("Command cant be null");
        }

        String topic = switch (command.getOperationType()) {
            case CREATE -> KafkaTopics.CREATE;
            case UPDATE -> KafkaTopics.UPDATE;
            case DELETE -> KafkaTopics.DELETE;
            default -> throw new IllegalArgumentException("Invalid operation type " + command.getOperationType());
        };
        kafkaTemplate.send(
                topic,
                command.getTransferId() == null ? null : command.getTransferId().toString(),
                command
        );
    }

    public void sendGet(SuspiciousTransferQuery query) {
        if (query == null) {
            throw new IllegalArgumentException("Query cant be null");
        }
        kafkaTemplate.send(KafkaTopics.GET, query.getCorrelationId(), query);
    }

}

