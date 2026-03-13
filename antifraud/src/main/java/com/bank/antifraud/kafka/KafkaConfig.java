package com.bank.antifraud.kafka;

import com.bank.antifraud.kafka.dto.SuspiciousTransferCommand;
import com.bank.antifraud.kafka.exception.AntifraudExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.CommonErrorHandler;

@Configuration
@EnableKafka
public class KafkaConfig {
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, SuspiciousTransferCommand> kafkaListenerContainerFactory(
            ConsumerFactory<String, SuspiciousTransferCommand> consumerFactory,
            AntifraudExceptionHandler exceptionHandler
    ) {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, SuspiciousTransferCommand>();

        factory.setConsumerFactory(consumerFactory);
        factory.setCommonErrorHandler((CommonErrorHandler) exceptionHandler);

        return factory;
    }

}
