package ru.practicum.requests.controller;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.logging.Loggable;
import ru.practicum.requests.dto.RequestDto;
import ru.practicum.requests.model.RequestStatus;
import ru.practicum.requests.service.RequestService;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/request/client")
@RequiredArgsConstructor
@Validated
@Slf4j
public class PrivatRequestClientController {
    private final RequestService requestService;

    @Loggable
    @GetMapping("/event/{eventId}")
    public List<RequestDto> getEventRequests(@PathVariable @Positive Long eventId) {
        return requestService.getEventRequests(eventId);
    }

    @Loggable
    @GetMapping("/event/{eventId}/status")
    public Long countRequest(@PathVariable @Positive Long eventId,
                             @RequestParam @NotNull RequestStatus status) {
        return requestService.countRequest(eventId, status);
    }

    @Loggable
    @PostMapping
    public List<RequestDto> saveAllRequests(@RequestBody @NotNull List<RequestDto> requestDtoList) {
        return requestService.saveAllRequests(requestDtoList);
    }

    @Loggable
    @GetMapping
    public List<RequestDto> findAllRequests(@RequestBody @NotNull List<Long> requestIds) {
        return requestService.findAllRequests(requestIds);
    }

    @Loggable
    @GetMapping("/status")
    public List<RequestDto> findRequestByStatus(@RequestBody @NotNull List<Long> requestId,
                                                @RequestParam @NotNull RequestStatus status) {
        return requestService.findRequestByStatus(requestId, status);
    }

}
