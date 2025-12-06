package ru.practicum.user.controller;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.logging.Loggable;
import ru.practicum.user.dto.UserShortDto;
import ru.practicum.user.service.UserService;

import java.util.List;

@RestController
@AllArgsConstructor
@Validated
@RequestMapping("/admin/users/client")
@Slf4j
public class PrivatUserClientController {

    private final UserService userService;

    @Loggable
    @GetMapping("/{userId}")
    public UserShortDto getShortUserById(@Positive @PathVariable("userId") Long userId) {
        return userService.findShortUserById(userId);
    }

    @Loggable
    @GetMapping
    public List<UserShortDto> findByIdIn(@RequestParam(name = "ids", required = false) List<Long> ids) {
        return userService.findShortUserByIdIn(ids);
    }
}
