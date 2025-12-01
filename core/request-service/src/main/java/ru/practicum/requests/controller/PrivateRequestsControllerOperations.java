package ru.practicum.requests.controller;

import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.requests.dto.RequestDto;

import java.util.List;

public interface PrivateRequestsControllerOperations {
    @GetMapping
    List<RequestDto> getUserRequests(@PathVariable @Positive Long userId);

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    RequestDto addParticipationRequest(
            @PathVariable @Positive Long userId,
            @RequestParam @Positive Long eventId);

    @PatchMapping("/{requestId}/cancel")
    RequestDto cancelRequest(
            @PathVariable @Positive Long userId,
            @PathVariable @Positive Long requestId);
}
