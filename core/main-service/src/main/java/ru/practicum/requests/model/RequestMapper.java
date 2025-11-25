package ru.practicum.requests.model;


import ru.practicum.requests.dto.RequestDto;

import java.util.List;
import java.util.stream.Collectors;

public class RequestMapper {
    public static RequestDto toDto(Request request) {
        return RequestDto.builder()
                .id(request.getId())
                .created(request.getCreated())
                .event(request.getEvent().getId())
                .requester(request.getRequester().getId())
                .status(request.getStatus().toString())
                .build();
    }

    public static List<RequestDto> toDto(List<Request> request) {
        return request.stream()
                .map(RequestMapper::toDto)
                .collect(Collectors.toList());
    }

}