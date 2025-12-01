package ru.practicum.event.client;

import org.springframework.cloud.openfeign.FeignClient;
import ru.practicum.event.controller.PrivateEventControllerOperations;

@FeignClient(name = "event-service", path = "/users/{userId}/events", fallback = EventPrivateClientFallback.class)
public interface EventPrivateClient extends PrivateEventControllerOperations {
}
