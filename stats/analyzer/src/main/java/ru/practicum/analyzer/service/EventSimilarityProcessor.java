package ru.practicum.analyzer.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.analyzer.common.KafkaConfig;
import ru.practicum.analyzer.service.handlers.EventSimilarityHandler;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;

@Slf4j
@Component
public class EventSimilarityProcessor extends BaseProcessor {

    private final EventSimilarityHandler eventSimilarityHandler;

    @Autowired
    public EventSimilarityProcessor(KafkaConfig kafkaConfig, EventSimilarityHandler eventSimilarityHandler) {
        super(kafkaConfig.getSimilarityConsumer().getProperties(),
                kafkaConfig.getSimilarityConsumer().getTopic(),
                kafkaConfig.getSimilarityConsumer().getPollTimeout());
        this.eventSimilarityHandler = eventSimilarityHandler;
    }

    @Override
    public void handleRecord(ConsumerRecord<String, SpecificRecordBase> record) {
        log.trace("<<< Получено сообщение топика = {}, партиция = {}, смещение = {}, значение: {}\n",
                record.topic(), record.partition(), record.offset(), record.value());
        log.debug("***************************************************************");
        log.debug(">>> Сообщение о сходстве мероприятий: <<< {}", record.value());
        log.debug("***************************************************************");
        if (record.value() instanceof EventSimilarityAvro eventSimilarityAvro) {

            eventSimilarityHandler.handleRecord(eventSimilarityAvro);

        }
    }
}