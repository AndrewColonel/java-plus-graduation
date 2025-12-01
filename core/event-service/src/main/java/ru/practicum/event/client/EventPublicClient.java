package ru.practicum.event.client;

import org.springframework.cloud.openfeign.FeignClient;
import ru.practicum.event.controller.PublicEventControllerOperations;

@FeignClient(name = "event-service", path = "/events", fallback = EventPublicClientFallback.class)
public interface EventPublicClient extends PublicEventControllerOperations {
}
