package ru.practicum.analyzer.service.handlers;

import ru.practicum.ewm.stats.avro.EventSimilarityAvro;

public interface EventSimilarityHandler {

    void handleRecord(EventSimilarityAvro eventSimilarityAvro);
}
