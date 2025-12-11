package ru.practicum.analyzer.service.handlers;

import ru.practicum.ewm.stats.avro.UserActionAvro;

public interface UserActionHandler {

    void handleRecord(UserActionAvro userActionAvro);
}
