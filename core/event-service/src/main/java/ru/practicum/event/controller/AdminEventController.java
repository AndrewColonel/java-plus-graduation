package ru.practicum.event.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.service.EventService;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.UpdateEventAdminRequest;
import ru.practicum.event.model.RequestAdminParams;

import java.util.List;

@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
@Slf4j
public class AdminEventController implements AdminEventControllerOperations {

    private final EventService eventService;

    @GetMapping
    @Override
    public List<EventFullDto> getAllEvents(
            @ModelAttribute RequestAdminParams params
    ) {
        log.warn(">>> AdminEventController: GET /admin/events");
        log.warn(">>> Поиск событий по параметрам : {}", params);
        List<EventFullDto> allEvents = eventService.getAllEvents(params);
        log.warn("ИТОГ: найден список событий {}", allEvents);
        return allEvents;
    }


    @PatchMapping("/{eventId}")
    @Override
    public EventFullDto updateEvent(
            @PathVariable Long eventId,
            @RequestBody @Valid UpdateEventAdminRequest request
    ) {
        log.info(">>> AdminEventController: PATCH /admin/events/{}", eventId);
        log.info(">>> Изменение данных события с ID = {} на {}", eventId, request);
        EventFullDto dto = eventService.updateEvent(eventId, request);
        log.warn("ИТОГ: данные события с ID = {} изменены на {}", eventId, dto);
        return dto;
    }
}

