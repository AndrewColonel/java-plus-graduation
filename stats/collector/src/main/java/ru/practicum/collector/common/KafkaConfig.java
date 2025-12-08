package ru.practicum.collector.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Properties;

@Getter
@AllArgsConstructor
@ConfigurationProperties(prefix = "collector.kafka")
public class KafkaConfig {

    private final ProducerConfig producerConfig;

    @Getter
    @Setter
    @AllArgsConstructor
    public static class ProducerConfig {
        private Properties properties;
        private String useractiontopic;
    }
 }
