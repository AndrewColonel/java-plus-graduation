package ru.practicum.analyzer.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.analyzer.common.KafkaConfig;
import ru.practicum.analyzer.service.handlers.UserActionHandler;
import ru.practicum.ewm.stats.avro.UserActionAvro;

@Slf4j
@Component
public class UserActionProcessor extends BaseProcessor {

    private final UserActionHandler userActionHandler;

    @Autowired
    public UserActionProcessor(KafkaConfig kafkaConfig, UserActionHandler userActionHandler) {
        super(kafkaConfig.getActionConsumer().getProperties(),
                kafkaConfig.getActionConsumer().getTopic(),
                kafkaConfig.getActionConsumer().getPollTimeout());
        this.userActionHandler = userActionHandler;
    }

    @Override
    public void handleRecord(ConsumerRecord<String, SpecificRecordBase> record) {
        log.trace("<<< Получено сообщение топика = {}, партиция = {}, смещение = {}, значение: {}\n",
                record.topic(), record.partition(), record.offset(), record.value());
        log.debug("***************************************************************");
        log.debug(">>> Сообщение о дейсвиях пользователя: <<< {}", record.value());
        log.debug("***************************************************************");
        if (record.value() instanceof UserActionAvro userActionAvro) {

            userActionHandler.handleRecord(userActionAvro);

        }
    }
}