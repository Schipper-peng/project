package com.bank.antifraud.kafka.consumer;

import com.bank.antifraud.kafka.KafkaTopics;
import com.bank.antifraud.kafka.dto.SuspiciousTransferCommand;
import com.bank.antifraud.service.SuspiciousTransferService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SuspiciousTransferConsumer {
    private final SuspiciousTransferService service;

    @KafkaListener(
            topics = "${app.kafka.topics.suspicious-transfer-command}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )

    public void listen(SuspiciousTransferCommand command) {
        log.info("Received SuspiciousTransferCommand: {}", command);

        switch (command.getOperationType()) {
            case CREATE -> service.handleCreate(command);
            case UPDATE -> service.handleUpdate(command);
            case DELETE -> service.handleDelete(command);
            default -> throw new IllegalArgumentException("Invalid operation type" + command.getOperationType());
        }
    }


    @KafkaListener(topics = KafkaTopics.GET)
    public void onGet(String json) {}

}
