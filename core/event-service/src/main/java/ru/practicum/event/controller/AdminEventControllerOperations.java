package ru.practicum.event.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.UpdateEventAdminRequest;
import ru.practicum.event.model.RequestAdminParams;

import java.util.List;

public interface AdminEventControllerOperations {
    @GetMapping
    List<EventFullDto> getAllEvents(
            @ModelAttribute RequestAdminParams params
    );

    @PatchMapping("/{eventId}")
    EventFullDto updateEvent(
            @PathVariable Long eventId,
            @RequestBody @Valid UpdateEventAdminRequest request
    );
}
