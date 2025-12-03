package ru.practicum.user.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.user.dto.UserDto;

@Slf4j
@Component
public class UserAdminServiceFallback implements UserAdminClient {
    @Override
    public UserDto getUserById(Long userId) {
        log.warn("Fallback UserClient response: сервис findById временно недоступен");
        return null;
    }
}
