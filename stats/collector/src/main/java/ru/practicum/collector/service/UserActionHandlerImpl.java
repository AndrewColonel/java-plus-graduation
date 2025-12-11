package ru.practicum.collector.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.stereotype.Component;


import ru.practicum.collector.common.KafkaCollectorProducer;
import ru.practicum.ewm.stats.avro.ActionTypeAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.ewm.stats.proto.UserActionProto;


import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.Future;

import static ru.practicum.collector.common.EnumMapper.toAvroEnum;

@Slf4j
@Component
@AllArgsConstructor
public class UserActionHandlerImpl implements UserActionHandler {

    private final KafkaCollectorProducer collectorProducer;

    @Override
    public void handle(UserActionProto userActionProto) {

        log.trace("|||-- преобразую UserActionProto в UserActionAvro");
        UserActionAvro userActionAvro = UserActionAvro.newBuilder()
                .setUserId(userActionProto.getUserId())
                .setEventId(userActionProto.getEventId())
                .setActionType(toAvroEnum(ActionTypeAvro.class,
                        userActionProto.getActionType().toString()))
                .setTimestamp(Instant.ofEpochSecond(userActionProto.getTimestamp().getSeconds(),
                        userActionProto.getTimestamp().getNanos()))

                .build();

        log.trace("|||-- объект UserActionProto с типом дейсвтия {}", userActionProto.getActionType());
        log.info("|||-- преобразован в UserActionAvro {}", userActionAvro);

        Producer<String, SpecificRecordBase> producer = collectorProducer.getProducer();
        String topic = collectorProducer.getTopic();
        ProducerRecord<String, SpecificRecordBase> record = new ProducerRecord<>(topic, userActionAvro);
        log.info("Объект Avro для отправки в брокер {} в топик {}", userActionAvro, topic);

        producer.send(record);
//        Future<RecordMetadata> metadataFuture = producer.send(record);
//        log.info("Состояние отправки: {} ", metadataFuture.isDone());
//        producer.flush();
//        log.info("Состояние отправки: {} ", metadataFuture.isDone());



//TODO генерация ошибки для tester.jar ))))))
        if (Objects.nonNull(userActionAvro)) throw new RuntimeException();




    }
}
