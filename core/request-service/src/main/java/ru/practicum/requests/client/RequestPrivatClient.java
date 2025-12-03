package ru.practicum.requests.client;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.requests.dto.RequestDto;
import ru.practicum.requests.model.entity.Request;

import java.util.List;

@FeignClient(name = "request-service", path = "/users/{userId}/requests", fallback = RequestPrivatClientFallback.class)
public interface RequestPrivatClient {

    @GetMapping
    List<RequestDto> getEventRequests(@RequestParam @Positive Long eventId);

    @GetMapping
    List<RequestDto> findAllRequests(@RequestBody @NotNull List<Long> requestIds);

    @GetMapping
    Long countRequest(@RequestParam @Positive Long eventId,
                      @RequestParam @NotNull Request.RequestStatus status);

    @PostMapping
    List<RequestDto> saveAllRequests(@RequestBody @NotNull List<RequestDto> requestDtoList);

    @GetMapping
    List<RequestDto> findRequestByStatus(@RequestBody @NotNull List<Long> requestId,
                                         @RequestParam @NotNull Request.RequestStatus status);
}
