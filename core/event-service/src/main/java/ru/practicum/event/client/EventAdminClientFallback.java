package ru.practicum.event.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.UpdateEventAdminRequest;
import ru.practicum.event.model.RequestAdminParams;

import java.util.List;

@Component
@Slf4j
public class EventAdminClientFallback implements EventAdminClient{

    @Override
    public List<EventFullDto> getAllEvents(RequestAdminParams params) {
        log.warn("Fallback EventAdminClient response: сервис getAllEvents временно недоступен");
        return List.of();
    }

    @Override
    public EventFullDto updateEvent(Long eventId, UpdateEventAdminRequest request) {
        log.warn("Fallback EventAdminClient response: сервис updateEvent временно недоступен");
        return null;
    }
}
