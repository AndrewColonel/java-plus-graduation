package ru.practicum.analyzer.service.handlers;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.analyzer.dal.entity.Similarity;
import ru.practicum.analyzer.dal.repository.SimilarityRepository;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Slf4j
@Component
@AllArgsConstructor
public class EventSimilarityProcessorHandler implements EventSimilarityHandler{

    private final SimilarityRepository repository;

    @Override
    public void handleRecord(EventSimilarityAvro eventSimilarityAvro) {
        LocalDateTime ts = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(eventSimilarityAvro.getTimestamp().toEpochMilli()),
                ZoneOffset.UTC
//                ZoneId.systemDefault()
        );

        Similarity similarity = repository.findByEvent1AndEvent2(
                        eventSimilarityAvro.getEventA(),
                        eventSimilarityAvro.getEventB()
                )
                .map(existing -> {
                    existing.setSimilarity(eventSimilarityAvro.getScore());
                    existing.setTs(ts);
                    return repository.save(existing);
                })
                .orElseGet(() -> {
                    Similarity newSimilarity = Similarity.builder()
                            .event1(eventSimilarityAvro.getEventA())
                            .event2(eventSimilarityAvro.getEventB())
                            .similarity(eventSimilarityAvro.getScore())
                            .ts(ts)
                            .build();
                    return repository.save(newSimilarity);
                });
        log.info("Сходство {} сохранено в БД", similarity);
    }
}
