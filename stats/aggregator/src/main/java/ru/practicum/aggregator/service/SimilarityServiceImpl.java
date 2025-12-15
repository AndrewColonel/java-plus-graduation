package ru.practicum.aggregator.service;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.avro.ActionTypeAvro;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

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
    private final Map<Long, Map<Long, Double>> actionMatrix = new TreeMap<>();
    // общие суммы весов каждого из мероприятий, где ключ — мероприятие,
    // а значение — сумма весов действий пользователей с ним.
    // Map<Event,SUM Weight>
    private final Map<Long, Double> commonWeightSumMap = new HashMap<>();

    @Override
    public Optional<EventSimilarityAvro> similarityProcessing(UserActionAvro userActionAvro) {

        Long eventId = userActionAvro.getEventId();
        Long userId = userActionAvro.getUserId();
        Double actionWeight = getActionWeight(userActionAvro.getActionType());

        log.trace("|||-- принято в обработку: {}", userActionAvro);
        log.trace("|||-- вес дейсвия {} равен: {}", userActionAvro.getActionType(), actionWeight);

        // собираем матрицу весов действий пользователей c мероприятиями в виде отображения. Map<Event, Map<User, Weight>>
        Map<Long, Double> userWeightMap = actionMatrix.computeIfAbsent(eventId, k -> new HashMap<>());
        // userWeightMap.merge(userId, actionWeight, Double::max);

        Double oldWeight = actionMatrix.get(eventId).getOrDefault(userId, 0.0);
        Double newWeight = userWeightMap.merge(userId, actionWeight, (old, newW) -> {
            Double result = Math.max(old, newW);
            log.trace("|||-- <<<<MAX WEIGHT COMPUTED>>>>> такой пользователь {} УЖЕ взаимодейстовал с мероприятием {}",
                    userId, eventId);
            log.trace("|||-- СТАРЫЙ  вес действия: {}", old);
            log.trace("|||-- НОВЫЙ максимальный вес действия: {}", result);

            return result;
        });
        log.trace("|||-- ТЕКУШЕЕ состояние вектора события:  {} = {}:", eventId, userWeightMap);
        log.trace("|||-- текущее состояние матрицы всех событий {}", actionMatrix);

        if (newWeight > oldWeight) {
            log.trace("||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||");
            log.trace("|||-- СТАРЫЙ  вес действия: {}", oldWeight);
            log.trace("|||-- НОВЫЙ  вес действия: {}", newWeight);
            log.trace("|||-- НАДО ПЕРЕСЧИТЫВАТь");

            // если частная сумма для весоы события уже была расчитана
            // посчитаем дельту между старым весом и обновлённым и увеличим на неё частную сумму:
            double deltaWeight = newWeight - oldWeight;
            commonWeightSumMap.computeIfPresent(eventId, (key, value) -> value + deltaWeight);

            // готовим знаменатель для расчета косинусного схождения - нужно пересчитать частную сумму для весов события
            commonWeightSumMap.computeIfAbsent(eventId, id ->
                    actionMatrix.getOrDefault(id, Collections.emptyMap()).values().stream()
                            .mapToDouble(Double::doubleValue)
                            // паттерн используется для безопасной обработки случая, когда мапа отсутствует:
                            .sum());

            log.trace("|||-- ОБЩАЯ СУММА ВЕСОВ для мероприятия: {} =  {}", eventId,
                    commonWeightSumMap.getOrDefault(eventId, 0.0));
            log.trace("|||-- ОБЩАЯ СУММА ВЕСОВ для ВСЕХ мероприятий: {} ", commonWeightSumMap);



        }


        return Optional.empty();

    }


    private Double getActionWeight(ActionTypeAvro actionTypeAvro) {
        // опрееделим вес для каждого типа действий пользователея
        return switch (actionTypeAvro) {
            case VIEW -> actionWeightView;
            case REGISTER -> actionWeightRegister;
            case LIKE -> actionWeightLike;
        };
    }


}