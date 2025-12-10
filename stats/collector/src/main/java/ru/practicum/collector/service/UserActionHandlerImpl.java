package ru.practicum.collector.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.stereotype.Component;



import ru.practicum.collector.common.KafkaProducer;
import ru.practicum.ewm.stats.avro.ActionTypeAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.grpc.stats.proto.UserActionProto;


import java.time.Instant;
import java.util.concurrent.Future;

import static ru.practicum.collector.common.EnumMapper.toAvroEnum;

@Slf4j
@Component
@AllArgsConstructor
public class UserActionHandlerImpl implements UserActionHandler {

    private final KafkaProducer kafkaProducer;

    @Override
    public void handle(UserActionProto userActionProto) {

        UserActionAvro userActionAvro = UserActionAvro.newBuilder()
                .setUserId(userActionProto.getUserId())
                .setEventId(userActionProto.getEventId())
                .setActionType(toAvroEnum(ActionTypeAvro.class,
                        userActionProto.getActionType().toString()))
                .setTimestamp(Instant.ofEpochSecond(userActionProto.getTimestamp().getSeconds(),
                        userActionProto.getTimestamp().getNanos()))

                .build();

        Producer<String, SpecificRecordBase> producer = kafkaProducer.getProducer();
        String topic = kafkaProducer.getUserActionTopic();
        ProducerRecord<String, SpecificRecordBase> record = new ProducerRecord<>(topic, userActionAvro);
        log.info("Объект Avro для отправки в брокер {} в топик {}", userActionAvro, topic);

        Future<RecordMetadata> metadataFuture = producer.send(record);
        log.info("Состояние отправки: {} ", metadataFuture.isDone());
        producer.flush();
        log.info("Состояние отправки: {} ", metadataFuture.isDone());
    }
}
