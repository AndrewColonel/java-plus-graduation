package ru.practicum.comment.client.event;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.comment.dto.ext.EventShortDto;


@FeignClient(name = "event-service", path = "/events/client", fallback = EventClientFallback.class)
public interface EventClient {

    @GetMapping("/short/{eventId}")
    EventShortDto getShortEventById(@PathVariable Long eventId);

}
