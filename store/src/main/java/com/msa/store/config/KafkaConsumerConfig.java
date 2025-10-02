package com.msa.store.config;

import com.msa.store.kafka.dto.AdminEventDto;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    /**
     * Kafka에서 수신한 관리자 이벤트를 역직렬화하기 위한 ConsumerFactory를 생성한다.
     * Store 서비스에서 사용하는 DTO 타입을 기본 타입으로 지정해 역직렬화 실패를 방지한다.
     */
    @Bean
    public ConsumerFactory<String, AdminEventDto> adminEventConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "com.msa.store.kafka.dto");
        props.put(JsonDeserializer.TYPE_MAPPINGS, "adminEvent:com.msa.store.kafka.dto.AdminEventDto");

        JsonDeserializer<AdminEventDto> valueDeserializer = new JsonDeserializer<>(AdminEventDto.class, false);
        valueDeserializer.addTrustedPackages("com.msa.store.kafka.dto");

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                valueDeserializer);
    }

    /**
     * 관리자 이벤트 전용 KafkaListenerContainerFactory를 생성하여 리스너가 올바른 ConsumerFactory를 사용하도록 한다.
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, AdminEventDto> adminEventKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, AdminEventDto> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(adminEventConsumerFactory());
        return factory;
    }
}