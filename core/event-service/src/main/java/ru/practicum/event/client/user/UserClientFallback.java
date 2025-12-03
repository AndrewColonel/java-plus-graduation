package ru.practicum.event.client.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.user.dto.UserDto;

@Slf4j
@Component
public class UserClientFallback implements UserClient {
    @Override
    public UserDto getUserById(Long userId) {
        log.warn("Fallback UserClient response: сервис findById временно недоступен");
        return null;
    }
}
