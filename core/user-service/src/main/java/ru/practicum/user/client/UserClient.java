package ru.practicum.user.client;

import org.springframework.cloud.openfeign.FeignClient;
import ru.practicum.user.controller.UserControllerOperations;

@FeignClient(name = "user-service", path = "/admin/users", fallback = UserServiceFallback.class)
public interface UserClient extends UserControllerOperations {
}
