package ru.practicum.event.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.service.EventService;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.UpdateEventUserRequest;
import ru.practicum.requests.dto.EventRequestStatusUpdateRequest;
import ru.practicum.requests.dto.EventRequestStatusUpdateResult;
import ru.practicum.requests.dto.RequestDto;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
@Slf4j
public class PrivateEventController implements PrivateEventControllerOperations {

    private final EventService eventService;

    @GetMapping
    @Override
    public List<EventShortDto> getAllEvents(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        log.warn(">>> UserEventController: GET /users/{}/events", userId);
        log.warn(">>> Заявки пользователя с ID = {} " +
                "на участие в чужих событиях", userId);
        List<EventShortDto> events = eventService.getAllEvents(userId, from, size);
        log.warn("ИТОГ: Список заявок пользователя : {}", events);
        return events;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public EventFullDto createEvent(
            @PathVariable Long userId,
            @RequestBody @Valid NewEventDto newEventDto
    ) {
        log.warn(">>> UserEventController: POST /users/{}/events", userId);
        log.warn(">>> Добавление события с параметрами {}", newEventDto);
        EventFullDto dto = eventService.createEvent(userId, newEventDto);
        log.warn("ИТОГ: Добавлнено событие {}", dto);
        return dto;
    }

    @GetMapping("/{eventId}")
    @Override
    public EventFullDto getEvent(
            @PathVariable Long userId,
            @PathVariable Long eventId
    ) {
        log.warn(">>> UserEventController: GET /users/{}/events/{}", userId, eventId);
        log.warn(">>> Получение информации о событии с ID = {} у пользователя с ID = {} ", eventId, userId);
        EventFullDto dto = eventService.getEvent(userId, eventId);
        log.warn("ИТОГ: Данные события {}", dto);
        return dto;
    }

    @PatchMapping("/{eventId}")
    @Override
    public EventFullDto updateEvent(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @RequestBody @Valid UpdateEventUserRequest request
    ) {
        log.warn(">>> UserEventController: PATCH /users/{}/events/{}", userId, eventId);
        log.warn(">>> Изменение информации о событии с ID = {} у пользователя с ID = {} ", eventId, userId);
        EventFullDto dto = eventService.updateEvent(userId, eventId, request);
        log.warn("ИТОГ: Данные изменены на {}", dto);
        return dto;
    }

    @GetMapping("/{eventId}/requests")
    @Override
    public List<RequestDto> getUsersEventRequests(@PathVariable Long userId,
                                                  @PathVariable Long eventId) {
        return eventService.getEventParticipants(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    @Override
    public EventRequestStatusUpdateResult changeRequestStatus(@PathVariable Long userId,
                                                              @PathVariable Long eventId,
                                                              @RequestBody @Valid EventRequestStatusUpdateRequest request) {
        return eventService.changeRequestStatus(userId, eventId, request);
    }
}
