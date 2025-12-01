package ru.practicum.user.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserDto;

import java.util.Collection;
import java.util.List;

public interface UserControllerOperations {
    @GetMapping
    Collection<UserDto> getAll(
            @RequestParam(name = "ids", required = false) List<Long> ids,
            @PositiveOrZero @RequestParam(name = "from", required = false, defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", required = false, defaultValue = "10") Integer size);

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    UserDto create(@Valid @RequestBody NewUserRequest newUserRequest);

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@Positive @PathVariable("userId") Long userId);

    @PatchMapping("/{userId}")
    void activate(@Positive @PathVariable("userId") Long userId,
                  @RequestParam(name = "activated", required = false) String approveStateString);


    @GetMapping("/{userId}")
    UserDto findById(@Positive @PathVariable("userId") Long userId);

}
