package com.bank.antifraud.kafka;

import com.bank.antifraud.dto.transfer.AccountTransferDto;
import com.bank.antifraud.dto.transfer.CardTransferDto;
import com.bank.antifraud.dto.transfer.PhoneTransferDto;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class KafkaConfig {

    private final KafkaProperties kafkaProperties;
    private final SslBundles sslBundles;

    @Bean
    public DefaultErrorHandler kafkaErrorHandler() {
        return new DefaultErrorHandler(new FixedBackOff(2000L, 2L));
    }

    @Bean
    public ConsumerFactory<String, AccountTransferDto> accountTransferConsumerFactory() {
        return new DefaultKafkaConsumerFactory<String, AccountTransferDto>(
                baseConsumerProps(),
                new StringDeserializer(),
                jsonDeserializer(AccountTransferDto.class)
        );
    }

    @Bean
    public ConsumerFactory<String, CardTransferDto> cardTransferConsumerFactory() {
        return new DefaultKafkaConsumerFactory<String, CardTransferDto>(
                baseConsumerProps(),
                new StringDeserializer(),
                jsonDeserializer(CardTransferDto.class)
        );
    }

    @Bean
    public ConsumerFactory<String, PhoneTransferDto> phoneTransferConsumerFactory() {
        return new DefaultKafkaConsumerFactory<String, PhoneTransferDto>(
                baseConsumerProps(),
                new StringDeserializer(),
                jsonDeserializer(PhoneTransferDto.class)
        );
    }

    @Bean
    public ProducerFactory<String, String> stringProducerFactory() {
        Map<String, Object> props = kafkaProperties.buildProducerProperties(sslBundles);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(stringProducerFactory());
    }


    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, AccountTransferDto>
    accountTransferKafkaListenerContainerFactory(DefaultErrorHandler errorHandler) {
        ConcurrentKafkaListenerContainerFactory<String, AccountTransferDto> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(accountTransferConsumerFactory());
        factory.setCommonErrorHandler(errorHandler);
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, CardTransferDto>
    cardTransferKafkaListenerContainerFactory(DefaultErrorHandler errorHandler) {
        ConcurrentKafkaListenerContainerFactory<String, CardTransferDto> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(cardTransferConsumerFactory());
        factory.setCommonErrorHandler(errorHandler);
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, PhoneTransferDto>
    phoneTransferKafkaListenerContainerFactory(DefaultErrorHandler errorHandler) {
        ConcurrentKafkaListenerContainerFactory<String, PhoneTransferDto> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(phoneTransferConsumerFactory());
        factory.setCommonErrorHandler(errorHandler);
        return factory;
    }

    private Map<String, Object> baseConsumerProps() {
        return kafkaProperties.buildConsumerProperties(sslBundles);
    }

    private <T> JsonDeserializer<T> jsonDeserializer(Class<T> clazz) {
        JsonDeserializer<T> deserializer = new JsonDeserializer<>(clazz, false);
        deserializer.addTrustedPackages("*");
        return deserializer;
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
