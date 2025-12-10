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

    @Value(value = "${aggregator.action.weight.view}")
    private Double ACTION_WEIGHT_VIEW;
    @Value(value = "${aggregator.action.weight.registered}")
    private Double ACTION_WEIGHT_REGISTER;
    @Value(value = "${aggregator.action.weight.like}")
    private Double ACTION_WEIGHT_LIKE;




    // матрица весов Map<Event, Map<User, Weight>>
    private final Map<Long, Map<Long, Double>> actionMatrix = new HashMap<>();

    @Override
    public Optional<EventSimilarityAvro> similarityProcessing(UserActionAvro userActionAvro) {

        System.out.println(ACTION_WEIGHT_VIEW);
        System.out.println(ACTION_WEIGHT_REGISTER);
        System.out.println(ACTION_WEIGHT_LIKE);




        // опрееделим вес для каждого типа действий пользователея
        Double actionWeight = switch (userActionAvro.getActionType()) {
            case ACTION_VIEW -> ACTION_WEIGHT_VIEW;
            case ACTION_REGISTER -> ACTION_WEIGHT_REGISTER;
            case ACTION_LIKE -> ACTION_WEIGHT_LIKE;
        };

        System.out.println("--------actionWeight----------------------");
        System.out.println(actionWeight);


        // собираем матрицу весов действий пользователей c мероприятиями в виде отображения.
        // Map<Event, Map<User, Weight>>
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


        System.out.println(actionMatrix);








        return Optional.empty();
    }
}
