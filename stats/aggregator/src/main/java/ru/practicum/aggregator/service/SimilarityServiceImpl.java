package ru.practicum.aggregator.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;

import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class SimilarityServiceImpl implements SimilarityService {

    @Override
    public Optional<EventSimilarityAvro> similarityCompute(UserActionAvro userAction) {

        return Optional.empty();
    }
}
