package com.bank.antifraud.kafka.producer;


import com.bank.antifraud.kafka.KafkaTopics;
import com.bank.antifraud.kafka.dto.SuspiciousTransferCommand;
import com.bank.antifraud.kafka.dto.SuspiciousTransferQuery;
import com.bank.antifraud.kafka.dto.SuspiciousTransferResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
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

        log.info("Sending command to topic={}, operationType={}, transferType={}, transferId={}",
                topic,
                command.getOperationType(),
                command.getTransferType(),
                command.getTransferId());

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
        log.info("Sending GET query to topic={}, correlationId={}",
                KafkaTopics.GET,
                query.getCorrelationId());

        kafkaTemplate.send(KafkaTopics.GET, query.getCorrelationId(), query);
    }
    public void sendReply(String replyTopic, String correlationId, SuspiciousTransferResponse response) {
        if (replyTopic == null || replyTopic.isBlank()) {
            throw new IllegalArgumentException("Reply topic can't be null or blank");
        }
        if (correlationId == null || correlationId.isBlank()) {
            throw new IllegalArgumentException("Correlation id can't be null or blank");
        }
        if (response == null) {
            throw new IllegalArgumentException("Response can't be null");
        }

        log.info("Sending reply to topic={}, correlationId={}, transfersCount={}",
                replyTopic, correlationId,
                response.getTransfers() == null ? 0 : response.getTransfers().size());

        kafkaTemplate.send(replyTopic, correlationId, response);
    }
}

