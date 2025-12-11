package ru.practicum.analyzer.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.*;

@Slf4j
@Component
public abstract class BaseProcessor implements Runnable {

    @Value(value = "${analyzer.kafka.offset-fix-count}")
    private int offSetFixCount;

    private final String topicConsumer;
    private final Consumer<String, SpecificRecordBase> consumer;
    private final Duration pollTimeout;

    private static final Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();

    public BaseProcessor(Properties properties, String topicConsumer, Duration pollTimeout) {
        this.consumer = new KafkaConsumer<String, SpecificRecordBase>(properties);
        this.topicConsumer = topicConsumer;
        this.pollTimeout = pollTimeout;
    }

    @Override
    public void run() {
        Runtime.getRuntime().addShutdownHook(new Thread(new Thread(() -> {
            log.info("Сработал хук на завершение JVM. Прерываем работу консьюмера");
            consumer.wakeup();
        })));

        try {
            consumer.subscribe(List.of(topicConsumer));
            log.info("Начинаю приём сообщений из топика: {}", topicConsumer);

            while (true) {

                ConsumerRecords<String, SpecificRecordBase> records = consumer.poll(pollTimeout);
                int count = 0;
                for (ConsumerRecord<String, SpecificRecordBase> record : records) {
                    handleRecord(record);
                    manageOffsets(record, count, consumer);
                    count++;
                }
                consumer.commitAsync();
            }
        } catch (WakeupException ignored) {
            log.warn("Вызван метод wakeup, poll будет прерван");
        } catch (Exception e) {
            log.error("Ошибка во время обработки событий от датчиков", e);
        } finally {
            try {
                consumer.commitSync(currentOffsets);
            } finally {
                log.info("Закрываем консьюмер");
                consumer.close();
            }
        }
    }

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

    public abstract void handleRecord(ConsumerRecord<String, SpecificRecordBase> record);

}
