package ru.practicum.collector.common;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.Producer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class KafkaProducer {

    private final KafkaConfig.ProducerConfig producerConfig;
    private Producer<String, SpecificRecordBase> producer;

    @Autowired
    public KafkaProducer(KafkaConfig kafkaConfig) {
        this.producerConfig = kafkaConfig.getProducerConfig();
    }

    public Producer<String, SpecificRecordBase> getProducer() {
        if (Objects.isNull(producer)) {
            producer = new org.apache.kafka.clients.producer.KafkaProducer<>(producerConfig.getProperties());
        }
        return producer;
    }

    public String getUserActionTopic() {
        return producerConfig.getTopic();
    }

    public void closeProducer() {
        if (Objects.nonNull(producer)) {
            producer.close();
        }
    }

}
