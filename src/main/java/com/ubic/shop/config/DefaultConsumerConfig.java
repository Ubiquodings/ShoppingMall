package com.ubic.shop.config;

import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;

import java.util.Map;

@EnableKafka
@Configuration
public class DefaultConsumerConfig {

    @Autowired
    private KafkaProperties kafkaProperties;

    /**
     * for <String, String>
     * */
    @Bean
    public Map<String, Object> defaultConsumerConfigs() {
        KafkaProperties.Consumer consumerProps = kafkaProperties.getConsumer();
        consumerProps.setKeyDeserializer(StringDeserializer.class);
        consumerProps.setValueDeserializer(StringDeserializer.class);

        return kafkaProperties.buildConsumerProperties();
    }

    @Bean
    public ConsumerFactory<String,String> defaultConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(defaultConsumerConfigs(), new StringDeserializer(), new StringDeserializer()); // 여기에 key/value deserilaizer 추가하면 될듯!
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String,String>> defaultKafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String,String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(defaultConsumerFactory());
        factory.setConcurrency(1);
        return factory;
    }

    /**
     * for <String, Long>
     * */
    @Bean
    public Map<String, Object> consumerConfigs() {
        KafkaProperties.Consumer consumerProps = kafkaProperties.getConsumer();
        consumerProps.setKeyDeserializer(StringDeserializer.class);
        consumerProps.setValueDeserializer(LongDeserializer.class);

        return kafkaProperties.buildConsumerProperties(); // ? KafkaProperties to Map
    }

    @Bean
    public ConsumerFactory<String,Long/*String,String*/> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs(), new StringDeserializer(), new LongDeserializer()); // 여기에 key/value deserilaizer 추가하면 될듯!
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String,Long>>/*ConcurrentKafkaListenerContainerFactory<String,Long>*/
    kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String,Long> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setConcurrency(1);

        return factory;
    }

}
