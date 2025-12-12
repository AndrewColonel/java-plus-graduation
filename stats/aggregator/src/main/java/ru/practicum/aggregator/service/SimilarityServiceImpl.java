package ru.practicum.aggregator.service;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;

import java.time.Instant;
import java.util.*;
import java.util.random.RandomGenerator;

@Slf4j
@Service
@Getter
public class SimilarityServiceImpl implements SimilarityService {

    // считываем из конфигурации параметры веса каждого дейсвтия пользователя
    @Value(value = "${aggregator.action.weight.view}")
    private Double actionWeightView;
    @Value(value = "${aggregator.action.weight.registered}")
    private Double actionWeightRegister;
    @Value(value = "${aggregator.action.weight.like}")
    private Double actionWeightLike;

    // матрица весов Map<Event, Map<User, Weight>>
    private final Map<Long, Map<Long, Double>> actionMatrix = new HashMap<>();

    @Override
    public Optional<EventSimilarityAvro> similarityProcessing(UserActionAvro userActionAvro) {

        // опрееделим вес для каждого типа действий пользователея
        Double actionWeight = switch (userActionAvro.getActionType()) {
            case VIEW -> actionWeightView;
            case REGISTER -> actionWeightRegister;
            case LIKE -> actionWeightLike;
        };
        log.trace("|||-- принято в обработку: {}", userActionAvro);
        log.trace("|||-- вес дейсвия {} равен: {}", userActionAvro.getActionType(), actionWeight);

        // собираем матрицу весов действий пользователей c мероприятиями в виде отображения.
        // Map<Event, Map<User, Weight>>
        if (actionMatrix.containsKey(userActionAvro.getEventId())) {
            Map<Long, Double> userWeightMap = actionMatrix.get(userActionAvro.getEventId());
            // если событие уже было в матрице, проверяю действия пользователя
            if (userWeightMap.containsKey(userActionAvro.getUserId())) {
                log.trace("|||-- <<<<MAX WEIGHT COMPUTED>>>>> такой пользователь {} УЖЕ взаимодейстовал с мероприятием {}",
                        userActionAvro.getUserId(), userActionAvro.getEventId());
                log.trace("|||-- СТАРЫЙ  вес действия: {}",
                        userWeightMap.get(userActionAvro.getUserId()));
                log.trace("|||-- ТЕКУШЕЕ состояние матрицы события {} -  {}:",userActionAvro.getEventId(), userWeightMap);
                // теоретически, значение веса может быть NULL
                if (Objects.nonNull(userWeightMap.get(userActionAvro.getUserId()))) {
                    // учитывается только действие с максимальным весом
                    userWeightMap.compute(userActionAvro.getUserId(),
                            (k, oldWeight) -> Math.max(oldWeight, actionWeight));
                }

                log.trace("|||-- НОВЫЙ максимальный вес действия: {}",
                        userWeightMap.get(userActionAvro.getUserId()));
                log.trace("|||-- НОВОЕ состояние матрицы события {} -  {}:",userActionAvro.getEventId(), userWeightMap);
            } else {
                // если это первое действие пользователя с мероприятием - записываю его в отображение
                userWeightMap.put(userActionAvro.getUserId(), actionWeight);
                log.trace("|||-- +++++NEW USER++++++ такой пользователь {} еще не взаимодейстовал с мероприятием {}",
                        userActionAvro.getUserId(), userActionAvro.getEventId());
                log.trace("|||-- вес действия: {}",
                        userWeightMap.get(userActionAvro.getUserId()));
            }
        } else {
            // события еще не было в матрице
            Map<Long, Double> userWeightMap = new HashMap<>();
            userWeightMap.put(userActionAvro.getUserId(), actionWeight);
            actionMatrix.put(userActionAvro.getEventId(), userWeightMap);
            log.trace("|||-- +++++NEW EVENT++++++ такого события {} еще не было в матрице", userActionAvro.getEventId());

        }
        log.trace("|||-- текущее состояние матрицы всех событий {}", actionMatrix);



        //TODO реализовать алгоритм вычисления сходства
        EventSimilarityAvro eventSimilarityAvro = EventSimilarityAvro.newBuilder()
                .setEventA(RandomGenerator.getDefault().nextLong(100))
                .setEventB(RandomGenerator.getDefault().nextLong(100))
                .setScore(RandomGenerator.getDefault().nextDouble(0.999999))
                .setTimestamp(Instant.now())

                .build();



        return Optional.of(eventSimilarityAvro);
    }
}
