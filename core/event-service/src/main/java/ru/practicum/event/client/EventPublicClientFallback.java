package ru.practicum.event.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;

import java.util.List;
import java.util.Set;

@Slf4j
@Component
public class EventPublicClientFallback implements EventPublicClient {

    @Override
    public EventFullDto getById(Long eventId) {
        log.warn("Fallback EventPublicClient response: сервис getById временно недоступен");
        return null;
    }

    @Override
    public Set<EventShortDto> getAllByIdIn(List<Long> eventIds) {
        log.warn("Fallback EventPublicClient response: сервис getAllByIdIn временно недоступен");
        return Set.of();
    }
}
