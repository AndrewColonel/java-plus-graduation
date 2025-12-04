package ru.practicum.requests.model;


import ru.practicum.requests.dto.RequestDto;
import ru.practicum.requests.model.entity.Request;

import java.util.List;
import java.util.stream.Collectors;

public class RequestMapper {
    public static RequestDto toDto(Request request) {
        return RequestDto.builder()
                .id(request.getId())
                .created(request.getCreated())
                .event(request.getEventId())
                .requester(request.getRequesterId())
                .status(request.getStatus().toString())
                .build();
    }

    public static List<RequestDto> toDto(List<Request> request) {
        return request.stream()
                .map(RequestMapper::toDto)
                .collect(Collectors.toList());
    }

    public static Request toEntity(RequestDto requestDto) {
        return Request.builder()
                .id(requestDto.getId())
                .created(requestDto.getCreated())
                .eventId(requestDto.getEvent())
                .requesterId(requestDto.getRequester())
                .status(RequestStatus.valueOf(requestDto.getStatus()))
                .build();
    }

    public static List<Request> toEntity(List<RequestDto> requestDtoList) {
        return requestDtoList.stream()
                .map(RequestMapper::toEntity)
                .toList();
    }


}