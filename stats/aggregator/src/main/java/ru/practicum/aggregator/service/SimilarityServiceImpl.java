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

    // общие суммы весов каждого из мероприятий, где ключ — мероприятие,
    // а значение — сумма весов действий пользователей с ним.
    // Map<Event,SUM Weight>
    private final Map<Long, Double> commonWeightSumMap = new HashMap<>();

    // сумма минимальных весов для каждой пары мероприятий. Тогда ключом будет одно из мероприятий,
    // а значением — ещё одно отображение, где ключ — второе мероприятие, а значение — сумма их минимальных весов:
    // `Map<Event, Map<Event, S_min>>`
    private final Map<Long, Map<Long, Double>> minWeightsSumsMap = new HashMap<>();


    @Override
    public List<EventSimilarityAvro> similarityProcessing(UserActionAvro userActionAvro) {
        Long eventId = userActionAvro.getEventId();
        Long userId = userActionAvro.getUserId();
        Double actionWeight = getActionWeight(userActionAvro.getActionType());

        log.trace("|||-- принято в обработку: {}", userActionAvro);
        log.trace("|||-- вес дейсвия {} равен: {}", userActionAvro.getActionType(), actionWeight);

        // 1. Сбор матрицs максимальных весов действий пользователя c мероприятиями
        // Обновляем матрицу весов действий пользователей c мероприятиями в виде отображения. Map<Event, Map<User, Weight>>
        Map<Long, Double> userWeightMap = actionMatrix.computeIfAbsent(eventId, k -> new HashMap<>());
        Double oldWeight = userWeightMap.getOrDefault(userId, 0.0);
        Double newWeight = Math.max(oldWeight, actionWeight);

        // нет изменений — выходим
        if (newWeight <= oldWeight) {
            return List.of(); // нет изменений — выходим
        }

        userWeightMap.put(userId, newWeight);
        log.trace("|||-- МАКСИМАЛЬНЫЙ вес обновлён: {} -> {}", oldWeight, newWeight);

        // посчитаем дельту между старым весом и обновлённым и увеличим на неё частную сумму:
        // Обновляем общую сумму весов для события
        double delta = newWeight - oldWeight;
        commonWeightSumMap.merge(eventId, delta, Double::sum);

        log.trace("|||-- Общая сумма весов для {}: {}", eventId, commonWeightSumMap.get(eventId));

        List<EventSimilarityAvro> eventSimilarityAvroList = new ArrayList<>();

        // делаем перебор, но только по событиям с userId
        for (Map.Entry<Long, Map<Long, Double>> entry : actionMatrix.entrySet()) {
            // пропускаем равный самому себе ID мероприятия
            Long otherEventId = entry.getKey();
            if (otherEventId.equals(eventId)) continue;

            //пропускаем событие с которым этот пользователь не взаимодейстовал
            Map<Long, Double> otherWeights = entry.getValue();
            if (!otherWeights.containsKey(userId)) continue;

            long first = Math.min(eventId, entry.getKey());
            long second = Math.max(eventId, entry.getKey());

            // Готовим числитель
            double numerator = calculateNumerator(userWeightMap, otherWeights);
            log.trace("|||-- ЧИСЛИТЕЛЬ - ОБЩАЯ СУММА ВЕСОВ для {} и {} -> {}", first, second, numerator);

            // готовим знаменатель
            double denominator = Math.sqrt(commonWeightSumMap.getOrDefault(eventId, 0.0))
                    * Math.sqrt(commonWeightSumMap.getOrDefault(otherEventId, 0.0));
            log.trace("|||-- ЗНАМЕНАТЕЛЬ - ПРОИЗВЕДЕНИЕ КВАДРАТНЫХ КОРНЕЙ ОБЩИХ СУММ ВЕСОВ {} {} -> {}", first, second, denominator);

            double cosineSimilarity = (denominator == 0.0) ? 0.0 : numerator / denominator;
            log.trace("|||-- КОСИНУСНОЕ СХОЖДЕНИЕ -> {}", cosineSimilarity);

            eventSimilarityAvroList.add(
                    EventSimilarityAvro.newBuilder()
                            .setEventA(first)
                            .setEventB(second)
                            .setScore(cosineSimilarity)
                            .setTimestamp(Instant.now())
                            .build());
            log.trace("|||-- СУММА МИНИМАЛЬНЫХ ВЕСОВ для мероприятий {} {} -> {}", first, second, numerator);
        }
        log.trace("|||-- Список AVRO -> {}", eventSimilarityAvroList);
        return eventSimilarityAvroList;
    }

    // вспомогательные методы
    private double calculateNumerator(Map<Long, Double> weightsA, Map<Long, Double> weightsB) {
        return weightsA.entrySet().stream()
                .mapToDouble(entry -> {
                    Double weightB = weightsB.get(entry.getKey());
                    return (weightB != null) ? Math.min(entry.getValue(), weightB) : 0.0;
                })
                .sum();
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