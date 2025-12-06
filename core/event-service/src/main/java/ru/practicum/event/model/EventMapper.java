package ru.practicum.event.model;

import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.ext.CategoryDto;
import ru.practicum.event.dto.ext.UserShortDto;
import ru.practicum.event.model.entity.Event;

import java.util.Map;


public class EventMapper {
    public static EventShortDto toEventShortDto(Event event, Map<Long, CategoryDto> categoryDtoMap,
                                                Map<Long, UserShortDto> userShortDtoMap) {
        return EventShortDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .eventDate(event.getEventDate())
                .paid(event.getPaid())
                .title(event.getTitle())
                .category(categoryDtoMap.get(event.getCategory()))
                .initiator(userShortDtoMap.get(event.getInitiatorId()))
                .build();
    }


    public static EventShortDto toEventShortDto(Event event, CategoryDto categoryDto,
                                               UserShortDto userShortDto) {
        return EventShortDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .eventDate(event.getEventDate())
                .paid(event.getPaid())
                .title(event.getTitle())
                .category(categoryDto)
                .initiator(userShortDto)
                .build();
    }


    public static EventFullDto toEventFullDto(Event event, Map<Long, CategoryDto> categoryDtoMap,
                                              Map<Long, UserShortDto> userShortDtoMap) {
        return EventFullDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .annotation(event.getAnnotation())
                .category(categoryDtoMap.get(event.getCategory()))
                .paid(event.getPaid())
                .eventDate(event.getEventDate())
                .initiator(userShortDtoMap.get(event.getInitiatorId()))
                .description(event.getDescription())
                .participantLimit(event.getParticipantLimit())
                .state(event.getState())
                .createdOn(event.getCreatedOn())
                .publishedOn(event.getPublishedOn())
                .location(event.getLocation())
                .requestModeration(event.getRequestModeration())
                .views(0L)
                .confirmedRequests(0L)
                .build();
    }

    public static EventFullDto toEventFullDto(Event event, CategoryDto categoryDto,
                                              UserShortDto userShortDto) {
        return EventFullDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .annotation(event.getAnnotation())
                .category(categoryDto)
                .paid(event.getPaid())
                .eventDate(event.getEventDate())
                .initiator(userShortDto)
                .description(event.getDescription())
                .participantLimit(event.getParticipantLimit())
                .state(event.getState())
                .createdOn(event.getCreatedOn())
                .publishedOn(event.getPublishedOn())
                .location(event.getLocation())
                .requestModeration(event.getRequestModeration())
                .views(0L)
                .confirmedRequests(0L)
                .build();
    }



    public static Event toEvent(NewEventDto newEventDto) {
        return Event.builder()
                .annotation(newEventDto.getAnnotation())
                .description(newEventDto.getDescription())
                .eventDate(newEventDto.getEventDate())
                .location(newEventDto.getLocation())
                .paid(newEventDto.getPaid() != null ? newEventDto.getPaid() : false)
                .participantLimit(newEventDto.getParticipantLimit() != null ? newEventDto.getParticipantLimit() : 0)
                .requestModeration(newEventDto.getRequestModeration() != null ? newEventDto.getRequestModeration() : true)
                .title(newEventDto.getTitle())
                .build();
    }
}
