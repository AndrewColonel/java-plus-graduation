package ru.practicum.event.client;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.model.RequestPublicParams;

import java.util.List;
import java.util.Set;

@Slf4j
@Component
public class EventPublicClientFallback implements EventPublicClient {
    @Override
    public List<EventShortDto> getEvents(RequestPublicParams params, HttpServletRequest request) {
        log.warn("Fallback EventPublicClient response: сервис getEvents временно недоступен");
        return List.of();
    }

    @Override
    public EventFullDto getEventById(Long eventId, HttpServletRequest request) {
        log.warn("Fallback EventPublicClient response: сервис getEventById временно недоступен");
        return null;
    }

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
