package ru.practicum.aggregator.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.practicum.aggregator.common.KafkaConfig;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;


import java.util.*;


/**
 * Класс AggregationStarter, ответственный за запуск агрегации данных.
 */
@Slf4j
@Component
public class AggregationStarter implements Runnable {

    @Value(value = "${aggregator.kafka.offset-fix-count}")
    private int offSetFixCount;

    private final SimilarityService similarityService;

    private final Producer<String, SpecificRecordBase> producer;
    private final KafkaConfig.ProducerConfig producerConfig;

    private final Consumer<String, SpecificRecordBase> consumer;
    private final KafkaConfig.ConsumerConfig consumerConfig;

    @Autowired
    public AggregationStarter(SimilarityService similarityService, KafkaConfig kafkaConfig) {
        this.similarityService = similarityService;

        this.producerConfig = kafkaConfig.getProducerConfig();
        this.consumerConfig = kafkaConfig.getConsumerConfig();

        this.producer = new KafkaProducer<>(producerConfig.getProperties());
        this.consumer = new KafkaConsumer<>(consumerConfig.getProperties());

    }

    private static final Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();

    /**
     * Метод для начала процесса агрегации данных.
     * Подписывается на топики для получения действий от пользователя,
     * расчитывает коминусное сходство и записывает в кафку.
     */
    @Override
    public void run() {
        // готовим консьюмер для получение данных SensorEventAvro из топика telemetry.sensors.v1
//        Consumer<String, SpecificRecordBase> consumer = kafkaClient.getConsumer();
        String topicConsumer = consumerConfig.getTopic();

        // готовим продьюсер для отправки подготовленных снапшотов SensorsSnapshotAvro в топик telemetry.snapshots.v1
//        Producer<String, SpecificRecordBase> producer = kafkaClient.getProducer();
        String topicProducer = producerConfig.getTopic();

        // регистрируем хук, в котором вызываем метод wakeup.
        Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));

        try {
            //подготовка к обработке данных, подписка на топик ...
            consumer.subscribe(List.of(topicConsumer));
            log.info("Начинаю приём сообщений из топика: {}", topicConsumer);
            // Цикл обработки событий
            while (true) {
                // реализация цикла опроса и обработка полученных данных
                ConsumerRecords<String, SpecificRecordBase> records = consumer.poll(consumerConfig.getPollTimeout());

                int count = 0;
                for (ConsumerRecord<String, SpecificRecordBase> record : records) {
                    // обрабатываем очередную запись
                        handleRecord(record, producer, topicProducer);
                    // фиксируем оффсеты обработанных записей, каждые 10 штук
                    manageOffsets(record, count, consumer);
                    count++;
                }
                // фиксируем максимальный оффсет обработанных записей
                consumer.commitAsync();
//                snapshots.clear();
            }
        } catch (WakeupException ignored) {
            log.warn("Вызван метод wakeup, poll будет прерван");
            // игнорируем - закрываем консьюмер и продюсер в блоке finally
        } catch (Exception e) {
            log.error("Ошибка во время обработки событий от датчиков", e);
        } finally {
            try {
                // Перед тем, как закрыть продюсер и консьюмер, нужно убедится,
                // что все сообщения, лежащие в буффере, отправлены и
                // все оффсеты обработанных сообщений зафиксированы
                // здесь нужно вызвать метод продюсера для сброса данных в буффере
                producer.flush();
                // здесь нужно вызвать метод консьюмера для фиксиции смещений
                consumer.commitSync(currentOffsets);
            } finally {
                log.info("Закрываем консьюмер");
                consumer.close();
                log.info("Закрываем продюсер");
                producer.close();
            }
        }
    }

    // вспомогательны метод для фиксации промежутоного оффсета (каждые 10 записей)
    private void manageOffsets(ConsumerRecord<String, SpecificRecordBase> record, int count,
                               Consumer<String, SpecificRecordBase> consumer) {
        // обновляем текущий оффсет для топика-партиции
        currentOffsets.put(
                new TopicPartition(record.topic(), record.partition()),
                new OffsetAndMetadata(record.offset() + 1)
        );
        if (count % offSetFixCount == 0) {
            consumer.commitAsync(currentOffsets, (offsets, exception) -> {
                if (Objects.nonNull(exception)) {
                    log.warn("Ошибка во время фиксации оффсетов: {}", offsets, exception);
                }
            });
        }
    }

    // подготовка и отправка снапшота
    private void handleRecord(
            ConsumerRecord<String, SpecificRecordBase> record,
            Producer<String, SpecificRecordBase> producer,
            String topicProducer) throws InterruptedException {
        log.info("<<< Получено сообщение топика = {}, партиция = {}, смещение = {}, значение: {}\n",
                record.topic(), record.partition(), record.offset(), record.value());
        if (record.value() instanceof UserActionAvro userActionAvro) {
           List<EventSimilarityAvro>  EventSimilarityAvroList = similarityService.similarityProcessing(userActionAvro);
            // если снапшот сформирован, то его надо отправить в брокер

            if (EventSimilarityAvroList.isEmpty())   log.info("<--- Сходство не было расчитано --->");
            EventSimilarityAvroList.forEach(eventSimilarityAvro -> {
                        ProducerRecord<String, SpecificRecordBase> producerRecord =
                                new ProducerRecord<>(topicProducer, eventSimilarityAvro);
                        log.info(">>> Расчитано сходство мероприятий {} для отправки в топик {}",
                                eventSimilarityAvro, topicProducer);
                        producer.send(producerRecord);
                    });
        }
    }
}