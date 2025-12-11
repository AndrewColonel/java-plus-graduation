package ru.practicum.analyzer.service.handlers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.practicum.analyzer.dal.entity.Interaction;
import ru.practicum.analyzer.dal.repository.InteractionRepository;
import ru.practicum.ewm.stats.avro.UserActionAvro;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserActionProcessorHandler implements UserActionHandler {

    private final InteractionRepository repository;

    // считываем из конфигурации параметры веса каждого дейсвтия пользователя
    @Value(value = "${analyzer.action.weight.view}")
    private Double actionWeightView;
    @Value(value = "${analyzer.action.weight.registered}")
    private Double actionWeightRegister;
    @Value(value = "${analyzer.action.weight.like}")
    private Double actionWeightLike;

    @Override
    public void handleRecord(UserActionAvro userActionAvro) {

        Interaction interaction = repository.save(Interaction.builder()
                .userId(userActionAvro.getUserId())
                .eventId(userActionAvro.getEventId())
                .rating(switch (userActionAvro.getActionType()) {
                    case VIEW -> actionWeightView;
                    case REGISTER -> actionWeightRegister;
                    case LIKE -> actionWeightLike;
                })
                .ts(LocalDateTime.from(userActionAvro.getTimestamp()))
                .build());

    }
}
