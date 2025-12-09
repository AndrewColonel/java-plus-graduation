package ru.practicum.aggregator.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.Properties;

@Getter
@AllArgsConstructor
@ConfigurationProperties(prefix = "aggregator.kafka")
public class KafkaConfig {

    private final ProducerConfig producerConfig;
    private final ConsumerConfig consumerConfig;

    @Getter
    @Setter
    @AllArgsConstructor
    public static class ProducerConfig {
        private Properties properties;
        private String topic;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class ConsumerConfig {
        private Properties properties;
        private String topic;
        private Duration pollTimeout;
    }
}


