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
        log.warn("!==============================================================================|");
        log.warn("Fallback RequestClient response: сервис getEventRequests временно недоступен");
        log.warn("!==============================================================================|");
        return List.of();
    }

    @Override
    public Long countRequest(Long eventId, RequestStatus status) {
        log.warn("!==============================================================================|");
        log.warn("Fallback RequestClient response: сервис countRequest временно недоступен");
        log.warn("!==============================================================================|");
        return 0L;
    }

    @Override
    public List<RequestDto> saveAllRequests(List<RequestDto> requestDtoList) {
        log.warn("!==============================================================================|");
        log.warn("Fallback RequestClient response: сервис saveAllRequests временно недоступен");
        log.warn("!==============================================================================|");
        return List.of();
    }

    @Override
    public List<RequestDto> findAllRequests(List<Long> requestIds) {
        log.warn("!==============================================================================|");
        log.warn("Fallback RequestClient response: сервис findAllRequests временно недоступен");
        log.warn("!==============================================================================|");
        return List.of();
    }

    @Override
    public List<RequestDto> findRequestByStatus(List<Long> requestId, RequestStatus status) {
        log.warn("!==============================================================================|");
        log.warn("Fallback RequestClient response: сервис findRequestByStatus временно недоступен");
        log.warn("!==============================================================================|");
        return List.of();
    }
}
