package ru.practicum.user.service;

import ru.practicum.user.dto.GetUserRequest;
import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.dto.UserShortDto;
import ru.practicum.user.model.ActiveUser;
import ru.practicum.user.model.entity.User;

import java.util.Collection;
import java.util.List;

public interface UserService {
    Collection<UserDto> getAllUsers(GetUserRequest getUserRequest);

    UserDto createUser(NewUserRequest newUserRequest);

    void deleteUser(Long userId);

    void activateUser(Long userId, ActiveUser approved);

    User getUser(Long id);

    UserDto findUserById(Long userId);

    UserShortDto findShortUserById(Long userId);

    List<UserShortDto> findShortUserByIdIn(List<Long> ids);

}
