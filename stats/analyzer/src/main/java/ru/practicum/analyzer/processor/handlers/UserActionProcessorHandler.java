package ru.practicum.analyzer.processor.handlers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.practicum.analyzer.dal.entity.Interaction;
import ru.practicum.analyzer.dal.repository.InteractionRepository;
import ru.practicum.ewm.stats.avro.UserActionAvro;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

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

        LocalDateTime ts = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(userActionAvro.getTimestamp().toEpochMilli()),
                ZoneOffset.UTC
//                ZoneId.systemDefault()
        );

        Double newWeight = switch (userActionAvro.getActionType()) {
            case VIEW -> actionWeightView;
            case REGISTER -> actionWeightRegister;
            case LIKE -> actionWeightLike;
        };

        Interaction interaction = repository.findByUserIdAndEventId(
                        userActionAvro.getUserId(),
                        userActionAvro.getEventId()
                )
                .map(existing -> {
                    existing.setRating(Math.max(newWeight, existing.getRating()));
                    existing.setTs(ts);
                    return repository.save(existing);
                })
                .orElseGet(() -> {
                    Interaction newInteraction = Interaction.builder()
                            .userId(userActionAvro.getUserId())
                            .eventId(userActionAvro.getEventId())
                            .rating(newWeight)
                            .ts(ts)
                            .build();
                    return repository.save(newInteraction);
                });
        log.info("Взаимодействие {} сохранено в БД", interaction);

    }
}
