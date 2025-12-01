package ru.practicum.requests.client;

import org.springframework.cloud.openfeign.FeignClient;
import ru.practicum.requests.controller.PrivateRequestsControllerOperations;

@FeignClient(name = "request-service", path = "/users/{userId}/requests", fallback = RequestPrivatClientFallback.class)
public interface RequestPrivatClient extends PrivateRequestsControllerOperations {
}
