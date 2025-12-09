package ru.practicum.aggregator.service;

import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;

import java.util.Optional;

public interface SimilarityService {
    Optional<EventSimilarityAvro> similarityCompute(UserActionAvro userAction);
}
