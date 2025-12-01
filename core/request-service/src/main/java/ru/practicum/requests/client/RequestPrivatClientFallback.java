package ru.practicum.requests.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.requests.dto.RequestDto;

import java.util.List;

@Slf4j
@Component
public class RequestPrivatClientFallback implements RequestPrivatClient {
    @Override
    public List<RequestDto> getUserRequests(Long userId) {
        log.warn("Fallback RequestPrivatClient response: сервис getUserRequests временно недоступен");
        return List.of();
    }

    @Override
    public RequestDto addParticipationRequest(Long userId, Long eventId) {
        log.warn("Fallback RequestPrivatClient response: сервис addParticipationRequest временно недоступен");
        return null;
    }

    @Override
    public RequestDto cancelRequest(Long userId, Long requestId) {
        log.warn("Fallback RequestPrivatClient response: сервис cancelRequest временно недоступен");
        return null;
    }
}
