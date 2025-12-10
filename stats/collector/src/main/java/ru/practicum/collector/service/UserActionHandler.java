package ru.practicum.collector.service;


import ru.practicum.grpc.stats.proto.UserActionProto;

public interface UserActionHandler {
    void handle(UserActionProto action);
}
