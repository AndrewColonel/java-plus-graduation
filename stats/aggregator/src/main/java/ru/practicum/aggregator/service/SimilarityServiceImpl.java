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

        // 2. Пользователь взаимодействоал с мерпориятием с изменением веса события
        if (newWeight > oldWeight) {
            log.trace("||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||");
            log.trace("|||-- СТАРЫЙ  вес действия: {}", oldWeight);
            log.trace("|||-- НОВЫЙ  вес действия: {}", newWeight);
            log.trace("|||-- НАДО ПЕРЕСЧИТЫВАТь ДЛЯ СОБЫТИЯ {}", eventId);

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


            // Готовим числитель - сумма минимальных весов для каждой пары мероприятий.
            List<EventSimilarityAvro> eventSimilarityAvroList = new ArrayList<>();
            for (Map.Entry<Long, Map<Long, Double>> actionMatrixEntry : actionMatrix.entrySet()) {
                // пропускаем равный самому себе ID мероприятия
                if (actionMatrixEntry.getKey().equals(eventId)) continue;

                //пропускаем событие с которым этот пользователь не взаимодейстовал
                if (!actionMatrixEntry.getValue().containsKey(userId)) continue;;

                double numerator = 0.0;
                for (Map.Entry<Long, Double> userWeightEntry : userWeightMap.entrySet()) {
                    numerator += Math.min(userWeightEntry.getValue(), actionMatrixEntry.getValue().getOrDefault(userWeightEntry.getKey(), 0.0));
                }

                long first = Math.min(eventId, actionMatrixEntry.getKey());
                long second = Math.max(eventId, actionMatrixEntry.getKey());


                log.trace("||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||");
                log.trace("|||-- СУММА МИНИМАЛЬНЫХ ВЕСОВ для мероприятий {} {} -> {}", first, second, numerator);


                    double denominator = Math.sqrt(commonWeightSumMap.getOrDefault(first, 0.0))
                            * Math.sqrt(commonWeightSumMap.getOrDefault(second, 0.0));

                    log.trace("|||-- ПРОИЗВЕДЕНИЕ КВАДРАТНЫХ КОРНЕЙ ОБЩИХ СУММ ВЕСОВ {} {} -> {}", first, second, denominator);

                    double cosineSimilarity = (denominator == 0.0) ? 0.0 : numerator / denominator;
                    log.trace("|||-- КОСИНУСНОЕ СХОЖДЕНИЕ -> {}", cosineSimilarity);

                    eventSimilarityAvroList.add(
                            EventSimilarityAvro.newBuilder()
                                    .setEventA(first)
                                    .setEventB(second)
                                    .setScore(cosineSimilarity)
                                    .setTimestamp(Instant.now())
                                    .build());


                log.trace("||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||");
                log.trace("|||-- СУММА МИНИМАЛЬНЫХ ВЕСОВ для мероприятий {} {} -> {}", first, second, numerator);
                log.trace("|||-- Список AVRO -> {}", eventSimilarityAvroList);




//                if (numerator == 0.0) {
//                    log.trace("|||-- дальнейший пересчет НЕ ТРЕБУЕТСЯ");
//                    continue;
//                }
//                put(eventId, actionMatrixEntry.getKey(), numerator);


            }

//            log.trace("|||-- СУММА МИНИМАЛЬНЫХ ВЕСОВ для ВСЕХ мероприятий -- |||");
//            for (Map.Entry<Long, Map<Long, Double>> minWeightsSumsMapEntry : minWeightsSumsMap.entrySet()) {
//                log.trace("|||-- для {} и {}", minWeightsSumsMapEntry.getKey(), minWeightsSumsMapEntry.getValue());
//            }


            //расчет косинусного схождения
//            List<EventSimilarityAvro> eventSimilarityAvroList = new ArrayList<>();
//            for (Map.Entry<Long, Map<Long, Double>> firstMinWeightSumEntry : minWeightsSumsMap.entrySet()) {
//                Long eventA = firstMinWeightSumEntry.getKey();
//
//                for (Map.Entry<Long, Double> secondMinWeightSumEntry : firstMinWeightSumEntry.getValue().entrySet()) {
//                    Long eventB = secondMinWeightSumEntry.getKey();
//                    Double numerator = secondMinWeightSumEntry.getValue();
//
//                    double denominator = Math.sqrt(commonWeightSumMap.getOrDefault(eventA, 0.0))
//                            * Math.sqrt(commonWeightSumMap.getOrDefault(eventB, 0.0));
//
//                    log.trace("|||-- ПРОИЗВЕДЕНИЕ КВАДРАТНЫХ КОРНЕЙ ОБЩИХ СУММ ВЕСОВ {} {} -> {}", eventA, eventB, denominator);
//
//                    double cosineSimilarity = (denominator == 0.0) ? 0.0 : numerator / denominator;
//                    log.trace("|||-- КОСИНУСНОЕ СХОЖДЕНИЕ -> {}", cosineSimilarity);
//
//                    eventSimilarityAvroList.add(
//                            EventSimilarityAvro.newBuilder()
//                                    .setEventA(eventA)
//                                    .setEventB(eventB)
//                                    .setScore(cosineSimilarity)
//                                    .setTimestamp(Instant.now())
//                                    .build());
//
//
//                }
//            }

            return eventSimilarityAvroList;

//            log.trace("|||-- СУММА МИНИМАЛЬНЫХ ВЕСОВ для ВСЕХ мероприятий -- |||");
//            for (Map.Entry<Long, Map<Long, Double>> minWeightsSumsMapEntry : minWeightsSumsMap.entrySet()) {
//                log.trace("|||-- для {} и {}", minWeightsSumsMapEntry.getKey(), minWeightsSumsMapEntry.getValue());
//            }

        } else {
            log.trace("||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||");
            log.trace("|||-- СТАРЫЙ  вес действия: {}", oldWeight);
            log.trace("|||-- НОВЫЙ  вес действия: {}", newWeight);
            log.trace("|||-- НЕ НАДО ПЕРЕСЧИТЫВАТь ДЛЯ СОБЫТИЯ {}", eventId);

        }


        return List.of();


    }


    // вспомогательные методы


    private Optional<EventSimilarityAvro> calculateCosineSimilarity(Long eventA, Long eventB, Double numerator) {

        long first = Math.min(eventA, eventB);
        long second = Math.max(eventA, eventB);

        double denominator = Math.sqrt(commonWeightSumMap.getOrDefault(first, 0.0))
                * Math.sqrt(commonWeightSumMap.getOrDefault(second, 0.0));

        log.trace("|||-- ПРОИЗВЕДЕНИЕ КВАДРАТНЫХ КОРНЕЙ ОБЩИХ СУММ ВЕСОВ {} {} -> {}", first, second, denominator);

        double cosineSimilarity = (denominator == 0.0) ? 0.0 : numerator / denominator;
        log.trace("|||-- КОСИНУСНОЕ СХОЖДЕНИЕ -> {}", cosineSimilarity);


        EventSimilarityAvro eventSimilarityAvro = EventSimilarityAvro.newBuilder()
                .setEventA(first)
                .setEventB(second)
                .setScore(cosineSimilarity)
                .setTimestamp(Instant.now())
                .build();

        return Optional.of(eventSimilarityAvro);
    }


    public void put(long eventA, long eventB, double sum) {
        long first = Math.min(eventA, eventB);
        long second = Math.max(eventA, eventB);

        minWeightsSumsMap
                .computeIfAbsent(first, e -> new HashMap<>())
                .put(second, sum);
    }

    public double get(long eventA, long eventB) {
        long first = Math.min(eventA, eventB);
        long second = Math.max(eventA, eventB);

        return minWeightsSumsMap
                .computeIfAbsent(first, e -> new HashMap<>())
                .getOrDefault(second, 0.0);
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