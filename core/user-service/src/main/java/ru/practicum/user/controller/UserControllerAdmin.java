package ru.practicum.user.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.exception.ValidationException;
import ru.practicum.logging.Loggable;
import ru.practicum.user.service.UserService;
import ru.practicum.user.dto.GetUserRequest;
import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.model.ActiveUser;

import java.util.Collection;
import java.util.List;

@RestController
@AllArgsConstructor
@Validated
@RequestMapping("/admin/users")
@Slf4j
public class UserControllerAdmin {

    private final UserService userService;

    @GetMapping
    public Collection<UserDto> getAll(
            @RequestParam(name = "ids", required = false) List<Long> ids,
            @PositiveOrZero @RequestParam(name = "from", required = false, defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", required = false, defaultValue = "10") Integer size) {
        log.warn(">>> UserControllerAdmin: GET /admin/users");
        log.warn(">>> запрос на получение списка пользователей с IDs = {} страница с {}, размер {}", ids, from, size);
        Collection<UserDto> dtoCollection = userService.getAllUsers(GetUserRequest.of(ids, from, size));
        log.warn("ИТОГ: Список пользователей {}", dtoCollection);
        return dtoCollection;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto create(@Valid @RequestBody NewUserRequest newUserRequest) {
        log.warn(">>> UserControllerAdmin: POST /admin/users");
        log.warn(">>> Запрос на создание пользователя {}", newUserRequest);
        UserDto dto = userService.createUser(newUserRequest);
        log.warn("ИТОГ: Создан пользователь {}", dto);
        return dto;
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@Positive @PathVariable("userId") Long userId) {
        log.warn(">>> UserControllerAdmin: DELETE /admin/users/{}", userId);
        log.warn(">>> Запрос на удаление пользователя с ID = {}", userId);
        userService.deleteUser(userId);
        log.warn("ИТОГ: удален пользователь с ID = {}", userId);
    }

    @PatchMapping("/{userId}")
    public void activate(@Positive @PathVariable("userId") Long userId,
                         @RequestParam(name = "activated", required = false) String approveStateString) {
        ActiveUser approved = ActiveUser.from(approveStateString).orElseThrow(
                () -> new ValidationException(
                        String.format("параметр активации %s введен неверно", approveStateString))
        );
        userService.activateUser(userId, approved);
    }

    @Loggable
    @GetMapping("/{userId}")
    public UserDto getUserById(@Positive @PathVariable("userId") Long userId) {
        return userService.findUserById(userId);
    }
}
