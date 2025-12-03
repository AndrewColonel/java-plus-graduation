package ru.practicum.requests.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.requests.dto.RequestDto;
import ru.practicum.requests.model.entity.Request;

import java.util.List;

@Slf4j
@Component
public class RequestPrivatClientFallback implements RequestPrivatClient {

    @Override
    public List<RequestDto> getEventRequests(Long eventId) {
        log.warn("Fallback RequestPrivatClient response: сервис getEventRequests временно недоступен");
        return List.of();
    }

    @Override
    public List<RequestDto> findAllRequests(List<Long> requestIds) {
        log.warn("Fallback RequestPrivatClient response: сервис findAllRequests временно недоступен");
        return List.of();
    }

    @Override
    public Long countRequest(Long eventId, Request.RequestStatus status) {
        log.warn("Fallback RequestPrivatClient response: сервис countRequest временно недоступен");
        return 0L;
    }

    @Override
    public List<RequestDto> saveAllRequests(List<RequestDto> requestDtoList) {
        log.warn("Fallback RequestPrivatClient response: сервис saveAllRequests временно недоступен");
        return List.of();
    }

    @Override
    public List<RequestDto> findRequestByStatus(List<Long> requestId, Request.RequestStatus status) {
        log.warn("Fallback RequestPrivatClient response: сервис getEventRequests временно недоступен");
        return List.of();
    }
}
