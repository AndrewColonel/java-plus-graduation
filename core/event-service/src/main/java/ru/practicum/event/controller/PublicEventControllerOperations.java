package ru.practicum.event.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.model.RequestPublicParams;

import java.util.List;

public interface PublicEventControllerOperations {
    @GetMapping
    List<EventShortDto> getEvents(
            @ModelAttribute RequestPublicParams params,
            HttpServletRequest request
    );

    @GetMapping("/{eventId}")
    EventFullDto getEventById(
            @PathVariable Long eventId,
            HttpServletRequest request
    );

    @GetMapping("/{eventId}")
    EventFullDto getById(@PathVariable Long eventId);
}
