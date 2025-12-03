package ru.practicum.event.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;

import java.util.List;
import java.util.Set;

@FeignClient(name = "event-service", path = "/events", fallback = EventPublicClientFallback.class)
public interface EventPublicClient {

    @GetMapping("/{eventId}")
     EventFullDto getById(@PathVariable Long eventId);

       @GetMapping
     Set<EventShortDto> getAllByIdIn(@RequestBody List<Long> eventIds);
}
