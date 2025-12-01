package ru.practicum.user.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserDto;

import java.util.Collection;
import java.util.List;

@Slf4j
@Component
public class UserServiceFallback implements UserClient {

    @Override
    public Collection<UserDto> getAll(List<Long> ids, Integer from, Integer size) {
        log.warn("Fallback UserClient response: сервис getAll временно недоступен");
        return List.of();
    }

    @Override
    public UserDto create(NewUserRequest newUserRequest) {
        log.warn("Fallback UserClient response: сервис create временно недоступен");
        return null;
    }

    @Override
    public void delete(Long userId) {
        log.warn("Fallback UserClient response: сервис delete временно недоступен");
    }

    @Override
    public void activate(Long userId, String approveStateString) {
        log.warn("Fallback UserClient response: сервис activate временно недоступен");
    }

    @Override
    public UserDto findById(Long userId) {
        log.warn("Fallback UserClient response: сервис findById временно недоступен");
        return null;
    }
}
