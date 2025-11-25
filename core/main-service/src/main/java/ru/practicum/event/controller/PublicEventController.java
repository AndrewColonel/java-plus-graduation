package ru.practicum.event.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.EventService;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.model.RequestPublicParams;

import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Slf4j
public class PublicEventController {

    private final EventService eventService;

    @GetMapping
    public List<EventShortDto> getEvents(
            @ModelAttribute RequestPublicParams params,
            HttpServletRequest request
    ) {
        return eventService.getAllEvents(params, request);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEventById(
            @PathVariable Long eventId,
            HttpServletRequest request
    ) {
        EventFullDto eventById = eventService.getEventById(eventId, request);
        return eventById;
    }
}
