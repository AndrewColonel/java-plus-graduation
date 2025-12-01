package ru.practicum.event.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.service.EventService;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.model.RequestPublicParams;
import ru.practicum.logging.Loggable;

import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Slf4j
public class PublicEventController implements PublicEventControllerOperations {

    private final EventService eventService;

    @Loggable
    @GetMapping
    @Override
    public List<EventShortDto> getEvents(
            @ModelAttribute RequestPublicParams params,
            HttpServletRequest request
    ) {
        return eventService.getAllEvents(params, request);
    }

    @Loggable
    @GetMapping("/{eventId}")
    @Override
    public EventFullDto getEventById(
            @PathVariable Long eventId,
            HttpServletRequest request
    ) {
        EventFullDto eventById = eventService.getEventById(eventId, request);
        return eventById;
    }

    @Loggable
    @GetMapping("/{eventId}")
    @Override
    public EventFullDto getById(@PathVariable Long eventId) {
        return eventService.getById(eventId);
    }
}
