package com.bank.antifraud.kafka.producer;


import com.bank.antifraud.kafka.KafkaTopics;
import com.bank.antifraud.kafka.dto.SuspiciousTransferCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SuspiciousTransferProducer {
    private final KafkaTemplate<String, SuspiciousTransferCommand> kafkaTemplate;



    public void send(SuspiciousTransferCommand command) {
        String topic = switch (command.getOperationType()) {
            case CREATE -> KafkaTopics.CREATE;
            case UPDATE -> KafkaTopics.UPDATE;
            case DELETE -> KafkaTopics.DELETE;
            case GET -> KafkaTopics.GET;
            default -> throw  new IllegalArgumentException("Invalid operation type" +  command.getOperationType());
        };
        kafkaTemplate.send(topic, command.getTransferId() == null ? null : command.getTransferId().toString(), command);

    }
}

