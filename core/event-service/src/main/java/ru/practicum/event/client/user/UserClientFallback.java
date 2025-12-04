package ru.practicum.event.client.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.event.dto.ext.UserShortDto;


import java.util.List;


@Slf4j
@Component
public class UserClientFallback implements UserClient {
    @Override
    public UserShortDto getShortUserById(Long userId) {
        log.warn("Fallback UserClient response: сервис getShortUserById временно недоступен");
        return null;
    }

    @Override
    public List<UserShortDto> findByIdIn(List<Long> ids) {
        log.warn("Fallback UserClient response: сервис findByIdIn временно недоступен");
        return List.of();
    }
}
