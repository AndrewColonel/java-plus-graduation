package ru.practicum.event.client.user;

import jakarta.validation.constraints.Positive;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.event.dto.ext.UserShortDto;


import java.util.List;


@FeignClient(name = "user-service", path = "/admin/users/client", fallback = UserClientFallback.class)
public interface UserClient {

    @GetMapping("/{userId}")
    UserShortDto getShortUserById(@Positive @PathVariable("userId") Long userId);

    @GetMapping
    List<UserShortDto> findByIdIn(@RequestParam(name = "ids", required = false) List<Long> ids);

}
