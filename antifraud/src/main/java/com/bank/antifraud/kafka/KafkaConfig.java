package com.bank.antifraud.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.autoconfigure.kafka.ConcurrentKafkaListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaConfig {
    @Bean
    public ConcurrentKafkaListenerContainerFactory<Object, Object> kafkaListenerContainerFactory(
            ConcurrentKafkaListenerContainerFactoryConfigurer configurer,
            ConsumerFactory<Object, Object> consumerFactory) {

        ConcurrentKafkaListenerContainerFactory<Object, Object> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        configurer.configure(factory, consumerFactory);
        factory.setCommonErrorHandler(new DefaultErrorHandler(new FixedBackOff(1000L, 2L)));
        return factory;
    }

    @Bean
    public NewTopic accountTransferTopic() {
        return TopicBuilder.name(KafkaTopics.ACCOUNT_TRANSFER).partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic cardTransferTopic() {
        return TopicBuilder.name(KafkaTopics.CARD_TRANSFER).partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic phoneTransferTopic() {
        return TopicBuilder.name(KafkaTopics.PHONE_TRANSFER).partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic suspiciousCreateTopic() {
        return TopicBuilder.name(KafkaTopics.CREATE).partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic suspiciousUpdateTopic() {
        return TopicBuilder.name(KafkaTopics.UPDATE).partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic suspiciousDeleteTopic() {
        return TopicBuilder.name(KafkaTopics.DELETE).partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic suspiciousGetTopic() {
        return TopicBuilder.name(KafkaTopics.GET).partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic suspiciousGetReplyTopic() {
        return TopicBuilder.name(KafkaTopics.GET_REPLY).partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic auditTopic() {
        return TopicBuilder.name(KafkaTopics.AUDIT).partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic fraudDecisionTopic() {
        return TopicBuilder.name(KafkaTopics.FRAUD_DECISION).partitions(1).replicas(1).build();
    }

}
