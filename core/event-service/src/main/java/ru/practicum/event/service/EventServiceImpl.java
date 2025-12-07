package ru.practicum.event.service;


import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.practicum.client.StatsClient;
import ru.practicum.event.client.category.CategoryClient;

import ru.practicum.compilations.dto.EndpointHitDto;
import ru.practicum.compilations.dto.ViewStatsDto;
import ru.practicum.event.client.user.UserClient;
import ru.practicum.event.dto.ext.*;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.event.repository.EventSpecification;
import ru.practicum.event.dto.*;
import ru.practicum.event.model.*;
import ru.practicum.event.model.entity.Event;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.event.client.request.RequestClient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static ru.practicum.event.model.EventMapper.toEventFullDto;
import static ru.practicum.event.model.EventMapper.toEventShortDto;

@Service
@AllArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final StatsClient statsClient;

    private final UserClient userClient;
    private final CategoryClient categoryClient;
    private final RequestClient requestClient;

    private static final String STATS_DATE_FROM = "2025-01-01 00:00:00";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    @Override
    public List<EventFullDto> getAllEvents(RequestAdminParams params) {
        Pageable pageable = PageRequest.of(params.getFrom() / params.getSize(), params.getSize());
        Specification<Event> spec = EventSpecification.byParams(params);
        List<Event> eventList = eventRepository.findAll(spec, pageable).getContent();

        Map<Long, CategoryDto> categoryDtoMap = getCategoryDtoMap(eventList);
        Map<Long, UserShortDto> userShortDtoMap = getUserShotDtoMap(eventList);

        List<EventFullDto> dtos = eventList.stream()
                .map(event -> toEventFullDto(event, categoryDtoMap, userShortDtoMap))
                .toList();
        setViewsAndConfirmedRequests(dtos);
        return dtos;
    }

    @Override
    public List<EventShortDto> getAllEvents(RequestPublicParams params, HttpServletRequest request) {
        if (params.getRangeStart() != null && params.getRangeEnd() != null
                && params.getRangeStart().isAfter(params.getRangeEnd())) {
            throw new ValidationException("rangeStart must not be after rangeEnd");
        }
        Pageable pageable;
        saveHit(request);

        if (params.getEventSort() == EventSort.EVENT_DATE) {
            pageable = PageRequest.of(params.getFrom() / params.getSize(), params.getSize(),
                    Sort.by("eventDate").descending());
        } else {
            pageable = PageRequest.of(params.getFrom() / params.getSize(), params.getSize());
        }

        Specification<Event> spec = EventSpecification.byParams(params);

        List<Event> eventList = eventRepository.findAll(spec, pageable).getContent();

        Map<Long, CategoryDto> categoryDtoMap = getCategoryDtoMap(eventList);
        Map<Long, UserShortDto> userShortDtoMap = getUserShotDtoMap(eventList);

        List<EventShortDto> dtos = eventList.stream()
                .map(event -> toEventShortDto(event, categoryDtoMap, userShortDtoMap))
                .toList();

        setViewsAndConfirmedRequests(dtos);

        if (params.getEventSort() != EventSort.EVENT_DATE) {
            dtos = dtos.stream()
                    .sorted(Comparator.comparing(EventShortDto::getViews).reversed())
                    .toList();
        }

        return dtos;
    }

    @Override
    public EventFullDto updateEvent(Long eventId, UpdateEventAdminRequest request) {
        Event event = getEventById(eventId);
        log.info(">>> PATCH updateEvent called for eventId={}, state={}", eventId, getEventById(eventId).getState());
        LocalDateTime publishedOn = event.getPublishedOn();
        if (request.getEventDate() != null && publishedOn != null &&
                !request.getEventDate().isAfter(publishedOn.plusHours(1))) {
            throw new ConflictException("Event date must be at least 1 hour after publication time");
        }

        applyUpdate(event, request);

        if (request.getStateAction() != null) {
            if (request.getStateAction().equals(StateAction.PUBLISH_EVENT) && !event.getState().equals(State.PENDING)) {
                throw new ConflictException("Event can be published only if it is in PENDING state");
            }
            if (request.getStateAction().equals(StateAction.REJECT_EVENT) && event.getState().equals(State.PUBLISHED)) {
                throw new ConflictException("Published event cannot be rejected");
            }

            switch (request.getStateAction()) {
                case PUBLISH_EVENT -> {
                    event.setState(State.PUBLISHED);
                    event.setPublishedOn(LocalDateTime.now());
                }
                case REJECT_EVENT -> event.setState(State.CANCELED);
            }
        }

        Event updated = eventRepository.save(event);
        CategoryDto categoryDto = categoryClient.getById(updated.getCategory());
        UserShortDto userShortDto = userClient.getShortUserById(updated.getInitiatorId());
        log.info("After update: Event id={}, state={}", updated.getId(), updated.getState());
        return toEventFullDto(updated, categoryDto, userShortDto);
    }

    @Override
    public List<EventShortDto> getAllEvents(Long userId, Integer from, Integer size) {
        UserShortDto user = userClient.getShortUserById(userId);

        Pageable pageable = PageRequest.of(from / size, size, Sort.by("createdOn").descending());

        Page<Event> page = eventRepository.findAllByInitiatorIdOrderByCreatedOnDesc(user.getId(), pageable);

        List<Event> events = page.getContent();

        if (events.isEmpty()) {
            return List.of();
        }

        Map<Long, CategoryDto> categoryDtoMap = getCategoryDtoMap(events);
        Map<Long, UserShortDto> userShortDtoMap = getUserShotDtoMap(events);

        List<EventShortDto> dtos = events.stream()
                .map(event -> toEventShortDto(event, categoryDtoMap, userShortDtoMap))
                .toList();

        setViewsAndConfirmedRequests(dtos);

        return dtos;
    }


    @Override
    public EventFullDto createEvent(Long userId, NewEventDto newEventDto) {
        if (newEventDto.getEventDate() == null ||
                !newEventDto.getEventDate().isAfter(LocalDateTime.now().plusHours(2))) {
            throw new ValidationException("Event date must be at least 2 hours from now");
        }
        Event event = EventMapper.toEvent(newEventDto);
        CategoryDto categoryDto = categoryClient.getById(newEventDto.getCategory());
        UserShortDto userShortDto = userClient.getShortUserById(userId);

        event.setCategory(categoryDto.getId());
        event.setInitiatorId(userShortDto.getId());
        event.setState(State.PENDING);
        event = eventRepository.save(event);

        return toEventFullDto(event, categoryDto, userShortDto);
    }

    @Override
    public EventFullDto getEvent(Long userId, Long eventId) {
        Event event = getEventById(eventId);
        UserShortDto userShortDto = userClient.getShortUserById(userId);
        CategoryDto categoryDto = categoryClient.getById(event.getCategory());
        if (!event.getInitiatorId().equals(userShortDto.getId())) {
            throw new ValidationException("User with id " + userId + " is not the initiator of event with id " + eventId);
        }

        EventFullDto dto = toEventFullDto(event, categoryDto, userShortDto);

        setViewsAndConfirmedRequests(List.of(dto));

        return dto;
    }

    @Override
    public EventFullDto updateEvent(Long userId, Long eventId, UpdateEventUserRequest request) {
        Event event = getEventById(eventId);
        if (!event.getInitiatorId().equals(userId)) {
            throw new ValidationException("User with id " + userId + " is not the initiator of event with id " + eventId);
        }
        if (!(event.getState() == State.CANCELED || event.getState() == State.PENDING)) {
            throw new ConflictException("Only canceled or pending events can be updated");
        }
        if (!event.getEventDate().isAfter(LocalDateTime.now().plusHours(2))) {
            throw new ConflictException("Event date must be at least 2 hours in the future");
        }

        applyUpdate(event, request);

        if (request.getStateAction() != null) {
            switch (request.getStateAction()) {
                case SEND_TO_REVIEW -> event.setState(State.PENDING);
                case CANCEL_REVIEW -> event.setState(State.CANCELED);
            }
        }

        Event updated = eventRepository.save(event);
        CategoryDto categoryDto = categoryClient.getById(updated.getCategory());
        UserShortDto userShortDto = userClient.getShortUserById(updated.getInitiatorId());
        return toEventFullDto(updated, categoryDto, userShortDto);
    }

    @Override
    public EventFullDto getEventById(Long eventId, HttpServletRequest request) {

        Event event = eventRepository.findEventByIdAndState(eventId, State.PUBLISHED)
                .orElseThrow(() -> new NotFoundException("Event not found with id: " + eventId));

        saveHit(request);

        Long statsViews = statsClient.getStats(
                        STATS_DATE_FROM,
                        LocalDateTime.now().format(DATE_TIME_FORMATTER),
                        List.of("/events/" + eventId), true)
                .stream()
                .findFirst()
                .map(ViewStatsDto::getHits)
                .orElse(0L);

        CategoryDto categoryDto = categoryClient.getById(event.getCategory());
        UserShortDto userShortDto = userClient.getShortUserById(event.getInitiatorId());

        EventFullDto dto = toEventFullDto(event, categoryDto, userShortDto);
        dto.setViews(statsViews);
        return dto;
    }

    @Override
    public EventFullDto getById(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found with id: " + eventId));

        CategoryDto categoryDto = categoryClient.getById(event.getCategory());
        UserShortDto userShortDto = userClient.getShortUserById(event.getInitiatorId());
        return toEventFullDto(event, categoryDto, userShortDto);

    }

    @Override
    public EventShortDto getShortEventById(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found with id: " + eventId));
        ;
        CategoryDto categoryDto = categoryClient.getById(event.getCategory());
        UserShortDto userShortDto = userClient.getShortUserById(event.getInitiatorId());
        return toEventShortDto(event, categoryDto, userShortDto);
    }

    @Override
    public List<EventShortDto> getShortEventByCategoryId(Long categoryId) {
        List<Event> eventList = eventRepository.findByCategory(categoryId);

        Map<Long, CategoryDto> categoryDtoMap = getCategoryDtoMap(eventList);
        Map<Long, UserShortDto> userShortDtoMap = getUserShotDtoMap(eventList);

        return eventList.stream()
                .map(event -> toEventShortDto(event, categoryDtoMap, userShortDtoMap))
                .toList();
    }

    @Override
    public List<RequestDto> getEventParticipants(Long userId, Long eventId) {
        Event event = getEventById(eventId);

        if (!event.getInitiatorId().equals(userId)) {
            throw new ValidationException(
                    "User with id " + userId + " is not the initiator of event with id " + eventId
            );
        }

        return requestClient.getEventRequests(event.getId());
    }

    @Override
    public EventRequestStatusUpdateResult changeRequestStatus(
            Long userId, Long eventId, EventRequestStatusUpdateRequest req
    ) {
        Event event = getEventById(eventId);

        if (!event.getInitiatorId().equals(userId)) {
            throw new ValidationException("User with id " + userId + " is not the initiator of event with id " + eventId);
        }

        RequestStatus targetStatus = req.getStatus();

        if ((targetStatus != RequestStatus.CONFIRMED && targetStatus != RequestStatus.REJECTED)) {
            throw new ValidationException("Status must be CONFIRMED or REJECTED");
        }

        List<RequestDto> requests = requestClient.findAllRequests(req.getRequestIds());

        for (RequestDto r : requests) {
            if (!r.getEvent().equals(eventId)) {
                throw new ValidationException("Request " + r.getId() + " does not belong to event " + eventId);
            }
            if (!r.getStatus().equals(RequestStatus.PENDING.toString())) {
                throw new ConflictException("Request must have status PENDING");
            }
        }

        int limit = event.getParticipantLimit();
        long alreadyConfirmed = requestClient.countRequest(eventId, RequestStatus.CONFIRMED);

        List<RequestDto> confirmedRequests = new ArrayList<>();
        List<RequestDto> rejectedRequests = new ArrayList<>();

        if (targetStatus == RequestStatus.REJECTED) {
            for (RequestDto r : requests) r.setStatus(RequestStatus.REJECTED.toString());
            rejectedRequests.addAll(requestClient.saveAllRequests(requests));
        } else {
            if (limit == 0) {
                for (RequestDto r : requests) {
                    r.setStatus(RequestStatus.CONFIRMED.toString());
                    confirmedRequests.add(r);
                }
                requestClient.saveAllRequests(confirmedRequests);
            } else {
                long remaining = limit - alreadyConfirmed;
                if (remaining <= 0) {
                    throw new ConflictException("The participant limit has been reached");
                }

                for (RequestDto r : requests) {
                    if (remaining > 0) {
                        r.setStatus(RequestStatus.CONFIRMED.toString());
                        confirmedRequests.add(r);
                        remaining--;
                    } else {
                        r.setStatus(RequestStatus.REJECTED.toString());
                        rejectedRequests.add(r);
                    }
                }
                requestClient.saveAllRequests(confirmedRequests);
                requestClient.saveAllRequests(rejectedRequests);
            }
        }

        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult();
        result.setConfirmedRequests(confirmedRequests);
        result.setRejectedRequests(rejectedRequests);
        return result;
    }

    @Override
    public Set<EventShortDto> getEventByIdIn(List<Long> eventIds) {
        List<Event> eventList = eventRepository.findAllById(eventIds);

        Map<Long, CategoryDto> categoryDtoMap = getCategoryDtoMap(eventList);
        Map<Long, UserShortDto> userShortDtoMap = getUserShotDtoMap(eventList);

        return eventList.stream()
                .map(event -> toEventShortDto(event, categoryDtoMap, userShortDtoMap))
                .collect(Collectors.toSet());
    }


    private Event getEventById(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found with id: " + eventId));
    }

    private Map<Long, CategoryDto> getCategoryDtoMap(List<Event> eventList) {
        Set<Long> categoryIds = eventList.stream().map(Event::getCategory).collect(Collectors.toSet());
        Set<CategoryDto> categoryDtoList = categoryClient.findByIdIn(categoryIds.stream().toList());
        return categoryDtoList.stream()
                .collect(Collectors.toMap(CategoryDto::getId, Function.identity()));
    }

    private Map<Long, UserShortDto> getUserShotDtoMap(List<Event> eventList) {
        List<Long> intiatorIds = eventList.stream().map(Event::getInitiatorId).toList();
        return userClient.findByIdIn(intiatorIds).stream()
                .collect(Collectors.toMap(UserShortDto::getId, Function.identity()));
    }


    private <T extends BaseDto> void setViewsAndConfirmedRequests(List<T> dto) {
        if (dto.isEmpty()) return;

        List<Long> eventIds = dto.stream().map(BaseDto::getId).toList();
        List<String> uris = eventIds.stream()
                .map(id -> "/events/" + id)
                .toList();

        List<ViewStatsDto> stats = statsClient.getStats(
                STATS_DATE_FROM,
                LocalDateTime.now().format(DATE_TIME_FORMATTER),
                uris,
                true
        );

        Map<String, Long> uriToViews = stats.stream()
                .collect(Collectors.toMap(ViewStatsDto::getUri, ViewStatsDto::getHits));

        Map<Long, Long> confirmedRequestsCount = requestClient.findRequestByStatus(
                        eventIds, RequestStatus.CONFIRMED)
                .stream()
                .collect(Collectors.groupingBy(RequestDto::getEvent, Collectors.counting()));

        for (BaseDto d : dto) {
            d.setViews(uriToViews.getOrDefault("/events/" + d.getId(), 0L));
            d.setConfirmedRequests(confirmedRequestsCount.getOrDefault(d.getId(), 0L));
        }
    }

    private void applyUpdate(Event event, UpdateEventRequest req) {
        if (req.getAnnotation() != null) event.setAnnotation(req.getAnnotation());
        if (req.getCategory() != null) event.setCategory(categoryClient.getById(req.getCategory()).getId());
        if (req.getDescription() != null) event.setDescription(req.getDescription());
        if (req.getEventDate() != null) event.setEventDate(req.getEventDate());
        if (req.getLocation() != null) event.setLocation(req.getLocation());
        if (req.getPaid() != null) event.setPaid(req.getPaid());
        if (req.getParticipantLimit() != null) event.setParticipantLimit(req.getParticipantLimit());
        if (req.getRequestModeration() != null) event.setRequestModeration(req.getRequestModeration());
        if (req.getTitle() != null) event.setTitle(req.getTitle());
    }

    private void saveHit(HttpServletRequest request) {
        EndpointHitDto endpointHitdto = EndpointHitDto.builder()
                .app("main-service")
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .build();

        statsClient.saveHit(endpointHitdto);
    }
}