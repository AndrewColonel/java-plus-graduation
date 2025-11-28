package ru.practicum.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.server.exception.NotFoundException;
import ru.practicum.user.dto.GetUserRequest;
import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserDto;

import java.util.Collection;

import static ru.practicum.user.UserMapper.toUser;
import static ru.practicum.user.UserMapper.toUserDto;

@Service
@AllArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public Collection<UserDto> getAllUsers(GetUserRequest getUserRequest) {
        log.warn("Обработка сервисом запроса на получение списка пользователей с IDs {}", getUserRequest.getIds());
        if (getUserRequest.getIds().isEmpty())
            return userRepository.findAll(getUserRequest.getPageable()).stream()
                    .map(UserMapper::toUserDto)
                    .toList();
        return userRepository.findByIdIn(getUserRequest.getIds(), getUserRequest.getPageable()).stream()
                .map(UserMapper::toUserDto)
                .toList();
    }

    @Override
    public UserDto createUser(NewUserRequest newUserRequest) {
        log.warn("Обработка сервисом запроса на создание пользователя");
        User save = userRepository.save(toUser(newUserRequest));
        log.warn("Обработка сервисом запроса на создание пользователя. Создан пользователь {}", save);
        return toUserDto(save);
    }

    @Override
    public void deleteUser(Long userId) {
        User user = getUser(userId);
        userRepository.delete(user);
    }

    @Override
    public void activateUser(Long userId, ActiveUser approved) {
        User user = getUser(userId);
        user.setActivated(approved == ActiveUser.TRUE);
        userRepository.save(user);
    }

    // вспомогательный метод
    public User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(String.format("Пользователь с ID %s не найден", userId)));

    }
}
