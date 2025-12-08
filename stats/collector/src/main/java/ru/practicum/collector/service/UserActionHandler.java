package ru.practicum.collector.service;

import ru.practicum.grpc.stats.action.UserActionProto;

public interface UserActionHandler {
    void handle(UserActionProto action);
}
