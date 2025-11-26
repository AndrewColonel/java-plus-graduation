package ru.practicum.client;

public class StatsServerUnavailable extends RuntimeException {
    public StatsServerUnavailable(String message) {
        super(message);
    }
}
