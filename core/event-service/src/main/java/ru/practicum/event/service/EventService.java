package ru.practicum.event.service;

import jakarta.servlet.http.HttpServletRequest;
import ru.practicum.event.dto.*;
import ru.practicum.event.model.RequestAdminParams;
import ru.practicum.event.model.RequestPublicParams;
import ru.practicum.requests.dto.EventRequestStatusUpdateRequest;
import ru.practicum.requests.dto.EventRequestStatusUpdateResult;
import ru.practicum.requests.dto.RequestDto;

import java.util.List;
import java.util.Set;

public interface EventService {

    List<EventFullDto> getAllEvents(RequestAdminParams params);

    EventFullDto updateEvent(Long eventId, UpdateEventAdminRequest request);

    List<EventShortDto> getAllEvents(Long userId, Integer from, Integer size);

    EventFullDto createEvent(Long userId, NewEventDto newEventDto);

    EventFullDto getEvent(Long userId, Long eventId);

    EventFullDto updateEvent(Long userId, Long eventId, UpdateEventUserRequest request);

    List<EventShortDto> getAllEvents(RequestPublicParams params, HttpServletRequest request);

    EventFullDto getEventById(Long eventId, HttpServletRequest request);

    EventFullDto getById(Long eventId);

    List<RequestDto> getEventParticipants(Long userId, Long eventId);

    EventRequestStatusUpdateResult changeRequestStatus(Long userId, Long eventId, EventRequestStatusUpdateRequest req);

    Set<EventFullDto> getEventByIdIn(List<Long> eventIds);
}
