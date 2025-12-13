package ru.practicum.collector.service;

import ru.practicum.ewm.stats.proto.UserActionProto;

public interface UserActionHandler {
    void handle(UserActionProto action);
}
