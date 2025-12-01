package ru.practicum.event.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.UpdateEventUserRequest;

import java.util.List;

public interface PrivateEventControllerOperations {
    @GetMapping
    List<EventShortDto> getAllEvents(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size
    );

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    EventFullDto createEvent(
            @PathVariable Long userId,
            @RequestBody @Valid NewEventDto newEventDto
    );

    @GetMapping("/{eventId}")
    EventFullDto getEvent(
            @PathVariable Long userId,
            @PathVariable Long eventId
    );

    @PatchMapping("/{eventId}")
    EventFullDto updateEvent(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @RequestBody @Valid UpdateEventUserRequest request
    );

    @GetMapping("/{eventId}/requests")
    List<RequestDto> getUsersEventRequests(@PathVariable Long userId,
                                           @PathVariable Long eventId);

    @PatchMapping("/{eventId}/requests")
    EventRequestStatusUpdateResult changeRequestStatus(@PathVariable Long userId,
                                                       @PathVariable Long eventId,
                                                       @RequestBody @Valid EventRequestStatusUpdateRequest request);
}
