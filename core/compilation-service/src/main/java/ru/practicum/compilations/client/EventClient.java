package ru.practicum.compilations.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RequestBody;
import ru.practicum.compilations.dto.ext.EventShortDto;


import java.util.List;
import java.util.Set;

@FeignClient(name = "event-service", path = "/events", fallback = EventClientFallback.class)
public interface EventClient {

    @GetMapping
    Set<EventShortDto> getAllByIdIn(@RequestBody List<Long> eventIds);
}
