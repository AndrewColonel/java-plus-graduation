package ru.practicum.comment.client.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.event.dto.EventFullDto;

@Slf4j
@Component
public class EventClientFallback implements EventClient {

    @Override
    public EventFullDto getById(Long eventId) {
        log.warn("Fallback EventClient response: сервис getById временно недоступен");
        return null;
    }

}
