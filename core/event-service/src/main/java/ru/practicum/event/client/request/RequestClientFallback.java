package ru.practicum.event.client.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.event.dto.ext.RequestDto;
import ru.practicum.event.dto.ext.RequestStatus;


import java.util.List;

@Slf4j
@Component
public class RequestClientFallback implements RequestClient {

    @Override
    public List<RequestDto> getEventRequests(Long eventId) {
        log.warn("Fallback RequestClient response: сервис getEventRequests временно недоступен");
        return List.of();
    }

    @Override
    public Long countRequest(Long eventId, RequestStatus status) {
        log.warn("Fallback RequestClient response: сервис countRequest временно недоступен");
        return 0L;
    }

    @Override
    public List<RequestDto> saveAllRequests(List<RequestDto> requestDtoList) {
        log.warn("Fallback RequestClient response: сервис saveAllRequests временно недоступен");
        return List.of();
    }

    @Override
    public List<RequestDto> findAllRequests(List<Long> requestIds) {
        log.warn("Fallback RequestClient response: сервис findAllRequests временно недоступен");
        return List.of();
    }

    @Override
    public List<RequestDto> findRequestByStatus(List<Long> requestId, RequestStatus status) {
        log.warn("Fallback RequestClient response: сервис findRequestByStatus временно недоступен");
        return List.of();
    }
}
