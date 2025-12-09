package ru.practicum.aggregator.service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;

import java.util.*;

@Slf4j
@Service
@AllArgsConstructor
public class SimilarityServiceImpl implements SimilarityService {

    @Value(value = "${agregator.action.weight.view}")
    private static double ACTION_VIEW_WEIGHT;
    @Value(value = "${agregator.action.weight.register}")
    private static double ACTION_REGISTER_WEIGHT;
    @Value(value = "${agregator.action.weight.like}")
    private static double ACTION_LIKE_WEIGHT;

    private final Map<Long, Map<Long, Double>> actionMatrix = new HashMap<>();

    @Override
    public Optional<EventSimilarityAvro> similarityProcessing(UserActionAvro userActionAvro) {

        // опрееделим вес для каждого типа действий пользователея
        Double actionWeight = switch (userActionAvro.getActionType()) {
            case ACTION_VIEW -> ACTION_VIEW_WEIGHT;
            case ACTION_REGISTER -> ACTION_REGISTER_WEIGHT;
            case ACTION_LIKE -> ACTION_LIKE_WEIGHT;
        };
        // собираем матрицу весов действий пользователей c мероприятиями в виде отображения.
        if (actionMatrix.containsKey(userActionAvro.getEventId())) {
            Map<Long, Double> userWeightMap = actionMatrix.get(userActionAvro.getEventId());
            // если событие уже было в матрице, проверяю действия пользователя
            if (userWeightMap.containsKey(userActionAvro.getUserId())) {
                if (Objects.nonNull(userWeightMap.get(userActionAvro.getUserId()))) {
                    // учитывается только действие с максимальным весом
                    userWeightMap.compute(userActionAvro.getUserId(),
                            (k, oldWeight) -> Math.max(oldWeight, actionWeight));
                }

            } else {
                // если это первое действие пользователя с мероприятием - записываю его в отображение
                userWeightMap.put(userActionAvro.getUserId(), actionWeight);
            }

        } else {
            // события еще не было в матрице
            Map<Long, Double> userWeightMap = new HashMap<>();
            userWeightMap.put(userActionAvro.getUserId(), actionWeight);
            actionMatrix.put(userActionAvro.getEventId(), userWeightMap);
        }

        return Optional.empty();
    }
}
