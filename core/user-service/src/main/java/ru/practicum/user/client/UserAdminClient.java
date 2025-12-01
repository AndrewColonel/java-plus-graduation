package ru.practicum.user.client;

import org.springframework.cloud.openfeign.FeignClient;
import ru.practicum.user.controller.UserControllerAdminOperations;

@FeignClient(name = "user-service", path = "/admin/users", fallback = UserAdminServiceFallback.class)
public interface UserAdminClient extends UserControllerAdminOperations {
}
