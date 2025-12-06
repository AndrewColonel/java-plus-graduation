package ru.practicum.event.client.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.ext.RequestDto;
import ru.practicum.event.dto.ext.RequestStatus;

import java.util.List;

@FeignClient(name = "request-service", path = "/requests/client", fallback = RequestClientFallback.class)
public interface RequestClient {

    @GetMapping("/event/{eventId}")
    List<RequestDto> getEventRequests(@PathVariable @Positive Long eventId);

    @GetMapping("/event/{eventId}/status")
    Long countRequest(@PathVariable @Positive Long eventId,
                      @RequestParam @NotNull RequestStatus status);

    @PostMapping
    List<RequestDto> saveAllRequests(@RequestBody @NotNull List<RequestDto> requestDtoList);

    @GetMapping
    List<RequestDto> findAllRequests(@RequestParam @NotNull List<Long> requestIds);

    @GetMapping("/status")
    List<RequestDto> findRequestByStatus(@RequestParam @NotNull List<Long> requestId,
                                         @RequestParam @NotNull RequestStatus status);

}
