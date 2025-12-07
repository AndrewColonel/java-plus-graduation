package ru.practicum.category.client.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.category.dto.ext.EventShortDto;

import java.util.List;


@Slf4j
@Component
public class EventClientFallback implements EventClient {

    @Override
    public List<EventShortDto> getShortEventByCategoryId(Long eventId) {
        log.warn("!==============================================================================|");
        log.warn("Fallback EventClient response: сервис getById временно недоступен");
        log.warn("!==============================================================================|");
        return null;
    }

}
