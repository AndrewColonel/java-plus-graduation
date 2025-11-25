package ru.practicum.user;

import ru.practicum.user.dto.GetUserRequest;
import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserDto;

import java.util.Collection;

public interface UserService {
    Collection<UserDto> getAllUsers(GetUserRequest getUserRequest);

    UserDto createUser(NewUserRequest newUserRequest);

    void deleteUser(Long userId);

    void activateUser(Long userId, ActiveUser approved);

    User getUser(Long id);
}
