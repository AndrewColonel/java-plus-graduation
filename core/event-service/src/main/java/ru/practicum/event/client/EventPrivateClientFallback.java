package ru.practicum.event.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.UpdateEventUserRequest;

import java.util.List;

@Slf4j
@Component
public class EventPrivateClientFallback implements EventPrivateClient {
    @Override
    public List<EventShortDto> getAllEvents(Long userId, Integer from, Integer size) {
        log.warn("Fallback EventPrivateClient response: сервис getAllEvents временно недоступен");
        return List.of();
    }

    @Override
    public EventFullDto createEvent(Long userId, NewEventDto newEventDto) {
        log.warn("Fallback EventPrivateClient response: сервис createEvent временно недоступен");
        return null;
    }

    @Override
    public EventFullDto getEvent(Long userId, Long eventId) {
        log.warn("Fallback EventPrivateClient response: сервис getEvent временно недоступен");
        return null;
    }

    @Override
    public EventFullDto updateEvent(Long userId, Long eventId, UpdateEventUserRequest request) {
        log.warn("Fallback EventPrivateClient response: сервис updateEvent временно недоступен");
        return null;
    }

    @Override
    public List<RequestDto> getUsersEventRequests(Long userId, Long eventId) {
        log.warn("Fallback EventPrivateClient response: сервис getUsersEventRequests временно недоступен");
        return List.of();
    }

    @Override
    public EventRequestStatusUpdateResult changeRequestStatus(Long userId, Long eventId, EventRequestStatusUpdateRequest request) {
        log.warn("Fallback EventPrivateClient response: сервис changeRequestStatus временно недоступен");
        return null;
    }
}
