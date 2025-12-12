package ru.practicum.analyzer.processor.handlers;

import ru.practicum.ewm.stats.avro.EventSimilarityAvro;

public interface EventSimilarityHandler {

    void handleRecord(EventSimilarityAvro eventSimilarityAvro);
}
