package ru.practicum.compilations.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.compilations.dto.ext.EventShortDto;

import java.util.List;
import java.util.Set;

@Slf4j
@Component
public class EventClientFallback implements EventClient {


    @Override
    public Set<EventShortDto> getAllByIdIn(List<Long> eventIds) {
        log.warn("|==========================================================================|");
        log.warn("Fallback EventClient response: сервис getAllByIdIn временно недоступен");
        log.warn("|==========================================================================|");
        return Set.of();
    }
}
