package ru.practicum.comment.client.user;

import jakarta.validation.constraints.Positive;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.comment.dto.UserDto;


@FeignClient(name = "user-service", path = "/admin/users", fallback = UserClientFallback.class)
public interface UserClient {

    @GetMapping("/{userId}")
    UserDto getUserById(@Positive @PathVariable("userId") Long userId);
}
