package ru.practicum.user.client;

import jakarta.validation.constraints.Positive;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.user.dto.UserDto;

@FeignClient(name = "user-service", path = "/admin/users", fallback = UserAdminServiceFallback.class)
public interface UserAdminClient {

    @GetMapping("/{userId}")
    UserDto getUserById(@Positive @PathVariable("userId") Long userId);
}
