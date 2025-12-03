package ru.practicum.requests.controller;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.logging.Loggable;
import ru.practicum.requests.dto.RequestDto;
import ru.practicum.requests.model.entity.Request;
import ru.practicum.requests.service.RequestService;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/requests")
@RequiredArgsConstructor
@Validated
@Slf4j
public class PrivateRequestsController {
    private final RequestService requestService;

    @GetMapping
      public List<RequestDto> getUserRequests(@PathVariable @Positive Long userId) {
        log.warn(">>> PrivateRequestsController: GET /users/{}/requests", userId);
        log.warn(">>> Получение информации о заявках пользователя с ID {} на участие в чужих событиях", userId);
        List<RequestDto> dtoList = requestService.getUserRequests(userId);
        log.warn("ИТОГ: Список заявок пользователя{}", dtoList);
        return dtoList;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
      public RequestDto addParticipationRequest(
            @PathVariable @Positive Long userId,
            @RequestParam @Positive Long eventId) {
        log.warn(">>> PrivateRequestsController: POST /users/{}/requests", userId);
        log.warn(">>> Добавление запроса от пользователя с ID {} на участие в событии с ID {}", userId, eventId);
        RequestDto dto = requestService.addParticipationRequest(userId, eventId);
        log.warn("ИТОГ: Добавлен запрос {}", dto);
        return dto;
    }

    @PatchMapping("/{requestId}/cancel")
    public RequestDto cancelRequest(
            @PathVariable @Positive Long userId,
            @PathVariable @Positive Long requestId) {
        log.warn(">>> PrivateRequestsController: PATCH /users/{}/requests/{}/cancel", userId, requestId);
        log.warn(">>> Отмена пользователем с ID {} своего запроса с ID {} на участие в событии", userId, requestId);
        RequestDto dto = requestService.cancelRequest(userId, requestId);
        log.warn("ИТОГ: отменен запрос {}", dto);
        return dto;
    }

    @Loggable
    @GetMapping
      public List<RequestDto> getEventRequests(@RequestParam @Positive Long eventId) {
        return requestService.getEventRequests(eventId);
    }

    @Loggable
    @GetMapping
       public List<RequestDto> findAllRequests(@RequestBody @NotNull List<Long> requestIds) {
        return requestService.findAllRequests(requestIds);
    }

    @Loggable
    @GetMapping
       public Long countRequest(@RequestParam @Positive Long eventId,
                             @RequestParam @NotNull Request.RequestStatus status) {
        return requestService.countRequest(eventId, status);
    }


    @Loggable
    @PostMapping
    public List<RequestDto> saveAllRequests(@RequestBody @NotNull List<RequestDto> requestDtoList) {
        return requestService.saveAllRequests(requestDtoList);
    }

    @Loggable
    @GetMapping
       public List<RequestDto> findRequestByStatus(@RequestBody @NotNull List<Long> requestId,
                                                @RequestParam @NotNull Request.RequestStatus status) {
        return requestService.findRequestByStatus(requestId, status);
    }
}