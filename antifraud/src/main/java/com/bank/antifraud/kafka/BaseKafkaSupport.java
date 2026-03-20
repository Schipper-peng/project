package com.bank.antifraud.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.slf4j.MDC;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
public class BaseKafkaSupport {
    protected final ObjectMapper objectMapper;

    protected BaseKafkaSupport(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    protected String header(Headers headers, String name) {
        if (headers == null) {
            return null;
        }
        Header header = headers.lastHeader(name);
        if (header == null || header.value() == null) {
            return null;
        }
        return new String(header.value(), StandardCharsets.UTF_8);
    }

    protected String correlationId(Headers headers) {
        String corrId = header(headers, KafkaHeader.CORRELATION_ID);
        return corrId != null ? corrId : UUID.randomUUID().toString();
    }

    protected <T> T readJson(String payload, Class<T> clazz) {
        try {
            return objectMapper.readValue(payload, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to deserialize message to " + clazz.getSimpleName(), e);
        }
    }

    protected String writeJson(Object payload) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize payload", e);
        }
    }

    protected void withMdc(String correlationId, Runnable runnable) {
        String previous = MDC.get(KafkaHeader.CORRELATION_ID);
        try {
            MDC.put(KafkaHeader.CORRELATION_ID, correlationId);
            runnable.run();
        } finally {
            if (previous == null) {
                MDC.remove(KafkaHeader.CORRELATION_ID);
            } else {
                MDC.put(KafkaHeader.CORRELATION_ID, previous);
            }
        }
    }
}
