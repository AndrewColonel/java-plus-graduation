package ru.practicum.event;


import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.practicum.category.CategoryService;
import ru.practicum.client.StatsClient;
import ru.practicum.compilations.dto.EndpointHitDto;
import ru.practicum.compilations.dto.ViewStatsDto;
import ru.practicum.event.dto.*;
import ru.practicum.event.model.*;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.requests.RequestRepository;
import ru.practicum.requests.dto.EventRequestStatusUpdateRequest;
import ru.practicum.requests.dto.EventRequestStatusUpdateResult;
import ru.practicum.requests.dto.RequestDto;
import ru.practicum.requests.model.Request;
import ru.practicum.requests.model.RequestMapper;
import ru.practicum.user.model.entity.User;
import ru.practicum.user.service.UserService;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final CategoryService categoryService;
    private final UserService userService;
    private final StatsClient statsClient;
    private final RequestRepository requestRepository;
    private static final String STATS_DATE_FROM = "2025-01-01 00:00:00";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    @Override
    public List<EventFullDto> getAllEvents(RequestAdminParams params) {
        Pageable pageable = PageRequest.of(params.getFrom() / params.getSize(), params.getSize());
        Specification<Event> spec = EventSpecification.byParams(params);

        List<EventFullDto> dtos = eventRepository.findAll(spec, pageable).getContent().stream()
                .map(EventMapper::toEventFullDto)
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

        List<EventShortDto> dtos = eventRepository.findAll(spec, pageable).getContent()
                .stream()
                .map(EventMapper::toEventShortDto)
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
        log.info("After update: Event id={}, state={}", updated.getId(), updated.getState());
        return EventMapper.toEventFullDto(updated);
    }

    @Override
    public List<EventShortDto> getAllEvents(Long userId, Integer from, Integer size) {
        User user = userService.getUser(userId);

        Pageable pageable = PageRequest.of(from / size, size, Sort.by("createdOn").descending());

        Page<Event> page = eventRepository.findAllByInitiatorOrderByCreatedOnDesc(user, pageable);

        List<Event> events = page.getContent();

        if (events.isEmpty()) {
            return List.of();
        }

        List<EventShortDto> dtos = events.stream()
                .map(EventMapper::toEventShortDto)
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
        event.setCategory(categoryService.getCategory(newEventDto.getCategory()));
        event.setInitiator(userService.getUser(userId));
        event.setState(State.PENDING);
        event = eventRepository.save(event);

        return EventMapper.toEventFullDto(event);
    }

    @Override
    public EventFullDto getEvent(Long userId, Long eventId) {
        Event event = getEventById(eventId);
        User user = userService.getUser(userId);
        if (!event.getInitiator().equals(user)) {
            throw new ValidationException("User with id " + userId + " is not the initiator of event with id " + eventId);
        }

        EventFullDto dto = EventMapper.toEventFullDto(event);

        setViewsAndConfirmedRequests(List.of(dto));

        return dto;
    }

    @Override
    public EventFullDto updateEvent(Long userId, Long eventId, UpdateEventUserRequest request) {
        Event event = getEventById(eventId);
        if (!event.getInitiator().getId().equals(userId)) {
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
        return EventMapper.toEventFullDto(updated);
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

        EventFullDto dto = EventMapper.toEventFullDto(event);
        dto.setViews(statsViews);
        return dto;
    }

    @Override
    public List<RequestDto> getEventParticipants(Long userId, Long eventId) {
        Event event = getEventById(eventId);

        if (!event.getInitiator().getId().equals(userId)) {
            throw new ValidationException(
                    "User with id " + userId + " is not the initiator of event with id " + eventId
            );
        }

        List<Request> requests = requestRepository.findAllByEventId(eventId);
        return requests.stream()
                .map(RequestMapper::toDto)
                .toList();
    }

    @Override
    public EventRequestStatusUpdateResult changeRequestStatus(
            Long userId, Long eventId, EventRequestStatusUpdateRequest req
    ) {
        Event event = getEventById(eventId);

        if (!event.getInitiator().getId().equals(userId)) {
            throw new ValidationException("User with id " + userId + " is not the initiator of event with id " + eventId);
        }

        Request.RequestStatus targetStatus = req.getStatus();

        if ((targetStatus != Request.RequestStatus.CONFIRMED && targetStatus != Request.RequestStatus.REJECTED)) {
            throw new ValidationException("Status must be CONFIRMED or REJECTED");
        }

        List<Request> requests = requestRepository.findAllById(req.getRequestIds());

        for (Request r : requests) {
            if (!r.getEventId().equals(eventId)) {
                throw new ValidationException("Request " + r.getId() + " does not belong to event " + eventId);
            }
            if (r.getStatus() != Request.RequestStatus.PENDING) {
                throw new ConflictException("Request must have status PENDING");
            }
        }

        int limit = event.getParticipantLimit();
        long alreadyConfirmed = requestRepository.countByEventIdAndStatus(eventId, Request.RequestStatus.CONFIRMED);

        List<Request> confirmedRequests = new ArrayList<>();
        List<Request> rejectedRequests = new ArrayList<>();

        if (targetStatus == Request.RequestStatus.REJECTED) {
            for (Request r : requests) r.setStatus(Request.RequestStatus.REJECTED);
            rejectedRequests.addAll(requestRepository.saveAll(requests));
        } else {
            if (limit == 0) {
                for (Request r : requests) {
                    r.setStatus(Request.RequestStatus.CONFIRMED);
                    confirmedRequests.add(r);
                }
                requestRepository.saveAll(confirmedRequests);
            } else {
                long remaining = limit - alreadyConfirmed;
                if (remaining <= 0) {
                    throw new ConflictException("The participant limit has been reached");
                }

                for (Request r : requests) {
                    if (remaining > 0) {
                        r.setStatus(Request.RequestStatus.CONFIRMED);
                        confirmedRequests.add(r);
                        remaining--;
                    } else {
                        r.setStatus(Request.RequestStatus.REJECTED);
                        rejectedRequests.add(r);
                    }
                }
                requestRepository.saveAll(confirmedRequests);
                requestRepository.saveAll(rejectedRequests);
            }
        }

        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult();
        result.setConfirmedRequests(confirmedRequests.stream().map(RequestMapper::toDto).toList());
        result.setRejectedRequests(rejectedRequests.stream().map(RequestMapper::toDto).toList());
        return result;
    }


    private Event getEventById(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found with id: " + eventId));
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

        Map<Long, Long> confirmedRequestsCount = requestRepository.findByEventIdInAndStatus(
                        eventIds, Request.RequestStatus.CONFIRMED)
                .stream()
                .collect(Collectors.groupingBy(Request::getEventId, Collectors.counting()));

        for (BaseDto d : dto) {
            d.setViews(uriToViews.getOrDefault("/events/" + d.getId(), 0L));
            d.setConfirmedRequests(confirmedRequestsCount.getOrDefault(d.getId(), 0L));
        }
    }

    private void applyUpdate(Event event, UpdateEventRequest req) {
        if (req.getAnnotation() != null) event.setAnnotation(req.getAnnotation());
        if (req.getCategory() != null) event.setCategory(categoryService.getCategory(req.getCategory()));
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