package ru.practicum.aggregator.service;

import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;

import java.util.List;
import java.util.Optional;

public interface SimilarityService {
    List<EventSimilarityAvro> similarityProcessing(UserActionAvro userAction);
}
