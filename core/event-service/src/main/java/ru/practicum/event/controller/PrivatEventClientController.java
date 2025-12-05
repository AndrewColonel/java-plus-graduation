package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.service.EventService;
import ru.practicum.logging.Loggable;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/events/client")
@RequiredArgsConstructor
@Slf4j
public class PrivatEventClientController {

    private final EventService eventService;
    @Loggable
    @GetMapping("/{eventId}")
    public EventFullDto getById(@PathVariable Long eventId) {
        return eventService.getById(eventId);
    }

    @Loggable
    @GetMapping("/short/{eventId}")
    public EventShortDto getShortEventById(@PathVariable Long eventId) {
        return eventService.getShortEventById(eventId);
    }

    @Loggable
    @GetMapping
    public Set<EventShortDto> getAllByIdIn(@RequestParam List<Long> eventIds) {
        return eventService.getEventByIdIn(eventIds);
    }
}
