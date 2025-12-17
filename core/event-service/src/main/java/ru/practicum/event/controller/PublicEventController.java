package ru.practicum.event.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.index.qual.Positive;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.ext.RecommendedEventDto;
import ru.practicum.event.service.EventService;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.model.RequestPublicParams;
import ru.practicum.ewm.stats.proto.RecommendedEventProto;
import ru.practicum.logging.Loggable;

import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Slf4j
public class PublicEventController {

    private final EventService eventService;

    @Loggable
    @GetMapping
    public List<EventShortDto> getEvents(
            @ModelAttribute RequestPublicParams params) {
        return eventService.getAllEvents(params);
    }

    @Loggable
    @GetMapping("/{eventId}")
    public EventFullDto getEventById(
            @PathVariable Long eventId,
            HttpServletRequest request
    ) {
        EventFullDto eventById = eventService.getEventById(eventId, request);
        return eventById;
    }

    @Loggable
    @GetMapping("/recommendations")
    public List<RecommendedEventDto> getRecommendations(
            @RequestHeader("X-EWM-USER-ID")
            @Positive long userId,
            @RequestParam(value = "maxResults", required = false, defaultValue = "10")
            @Positive int maxResults) {
        return eventService.getRecommendations(userId, maxResults);
    }

    @Loggable
    @PutMapping("/{eventId}/like")
    public void collectUserAction(
            @RequestHeader("X-EWM-USER-ID")
            @Positive long userId,
            @PathVariable long eventId) {
        eventService.collectUserAction(userId, eventId);
    }
}
