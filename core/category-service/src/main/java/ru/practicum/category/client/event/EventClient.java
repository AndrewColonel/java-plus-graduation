package ru.practicum.category.client.event;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.category.dto.ext.EventShortDto;

import java.util.List;


@FeignClient(name = "event-service", path = "/events/client", fallback = EventClientFallback.class)
public interface EventClient {

    @GetMapping("/category/{categoryId}")
    List<EventShortDto> getShortEventByCategoryId(@PathVariable Long categoryId);

}
