package ru.practicum.analyzer.service.handlers;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.analyzer.dal.entity.Similarity;
import ru.practicum.analyzer.dal.repository.SimilarityRepository;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;

import java.time.LocalDateTime;

@Slf4j
@Component
@AllArgsConstructor
public class EventSimilarityProcessorHandler implements EventSimilarityHandler{

    private final SimilarityRepository repository;

    @Override
    public void handleRecord(EventSimilarityAvro eventSimilarityAvro) {
        Similarity similarity =  repository.save(Similarity.builder()
                .event1(eventSimilarityAvro.getEventA())
                .event2(eventSimilarityAvro.getEventB())
                .similarity(eventSimilarityAvro.getScore())
                .ts(LocalDateTime.from(eventSimilarityAvro.getTimestamp()))
                .build());
        log.info("Сходство {} сохранено в БД", similarity);
    }
}
