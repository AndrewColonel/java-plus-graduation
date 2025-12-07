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
        log.warn("!==============================================================================|");
        log.warn("Fallback UserClient response: сервис getShortUserById временно недоступен");
        log.warn("!==============================================================================|");
        return null;
    }

    @Override
    public List<UserShortDto> findByIdIn(List<Long> ids) {
        log.warn("!==============================================================================|");
        log.warn("Fallback UserClient response: сервис findByIdIn временно недоступен");
        log.warn("!==============================================================================|");
        return List.of();
    }
}
