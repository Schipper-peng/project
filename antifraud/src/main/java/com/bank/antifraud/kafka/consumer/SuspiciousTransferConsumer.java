package com.bank.antifraud.kafka.consumer;

import com.bank.antifraud.kafka.dto.SuspiciousTransferCommand;
import com.bank.antifraud.kafka.dto.SuspiciousTransferQuery;
import com.bank.antifraud.service.SuspiciousTransferService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SuspiciousTransferConsumer {
    private final SuspiciousTransferService service;

    @KafkaListener(topics = "${app.kafka.topics.suspicious-transfers.create}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeCreate(SuspiciousTransferCommand command) {
        log.info("Received CREATE command: {}", command);
        service.handleCreate(command);
    }

    @KafkaListener(topics = "${app.kafka.topics.suspicious-transfers.update}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeUpdate(SuspiciousTransferCommand command) {
        log.info("Received UPDATE command: {}", command);
        service.handleUpdate(command);
    }

    @KafkaListener(topics = "${app.kafka.topics.suspicious-transfers.delete}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeDelete(SuspiciousTransferCommand command) {
        log.info("Received DELETE command: {}", command);
        service.handleDelete(command);
    }

    @KafkaListener(topics = "${app.kafka.topics.suspicious-transfers.get}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeGet(SuspiciousTransferQuery query) {
        log.info("Received GET query: {}", query);
        service.handleGet(query);
    }

}
