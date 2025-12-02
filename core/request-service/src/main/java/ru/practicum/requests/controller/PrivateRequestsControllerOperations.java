package ru.practicum.requests.controller;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import ru.practicum.requests.dto.RequestDto;
import ru.practicum.requests.model.entity.Request;

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

    @GetMapping
    List<RequestDto> getEventRequests(@RequestParam @Positive Long eventId);

    @GetMapping
    List<RequestDto> findAllRequests(@RequestBody @NotNull List<Long> requestIds);

    @GetMapping
    Long countRequest(@RequestParam @Positive Long eventId,
                             @RequestParam @NotNull Request.RequestStatus status);

    @PostMapping
    List<RequestDto> saveAllRequests(@RequestBody @NotNull List<RequestDto> requestDtoList);

    @GetMapping
    List<RequestDto> findRequestByStatus(@RequestBody @NotNull List<Long> requestId,
                                                @RequestParam @NotNull Request.RequestStatus status);
}
