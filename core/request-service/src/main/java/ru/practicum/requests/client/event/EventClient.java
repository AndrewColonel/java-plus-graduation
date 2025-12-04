package ru.practicum.requests.client.event;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.requests.dto.ext.EventFullDto;


@FeignClient(name = "event-service", path = "/events", fallback = EventClientFallback.class)
public interface EventClient {

    @GetMapping("/{eventId}")
    EventFullDto getById(@PathVariable Long eventId);

}
