package ru.practicum.aggregator.service;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;

import java.util.*;

@Slf4j
@Service
@Getter
public class SimilarityServiceImpl implements SimilarityService {

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

        System.out.println(actionWeightView);
        System.out.println(actionWeightRegister);
        System.out.println(actionWeightLike);




        // опрееделим вес для каждого типа действий пользователея
        Double actionWeight = switch (userActionAvro.getActionType()) {
            case ACTION_VIEW -> actionWeightView;
            case ACTION_REGISTER -> actionWeightRegister;
            case ACTION_LIKE -> actionWeightLike;
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
