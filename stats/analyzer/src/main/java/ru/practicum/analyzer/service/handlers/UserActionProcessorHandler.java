package ru.practicum.analyzer.service.handlers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.UserActionAvro;

@Slf4j
@Component
public class UserActionProcessorHandler implements UserActionHandler{
    @Override
    public void handleRecord(UserActionAvro userActionAvro) {
        // TODO
    }
}
