package ru.practicum.event.client;


import org.springframework.cloud.openfeign.FeignClient;
import ru.practicum.event.controller.AdminEventControllerOperations;

@FeignClient(name = "event-service", path = "/admin/events", fallback = EventAdminClientFallback.class)
public interface EventAdminClient extends AdminEventControllerOperations {
}
