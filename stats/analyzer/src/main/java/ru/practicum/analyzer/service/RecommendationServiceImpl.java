package ru.practicum.analyzer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.analyzer.dal.entity.Interaction;
import ru.practicum.analyzer.dal.entity.Similarity;
import ru.practicum.analyzer.dal.repository.InteractionRepository;
import ru.practicum.analyzer.dal.repository.SimilarityRepository;
import ru.practicum.ewm.stats.proto.InteractionsCountRequestProto;
import ru.practicum.ewm.stats.proto.RecommendedEventProto;
import ru.practicum.ewm.stats.proto.SimilarEventsRequestProto;
import ru.practicum.ewm.stats.proto.UserPredictionsRequestProto;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {

    private final InteractionRepository interactionRepository;
    private final SimilarityRepository similarityRepository;

    @Override
    // возвращает поток рекомендованных мероприятий для указанного пользователя:
    public Stream<RecommendedEventProto> getRecommendationsForUser(UserPredictionsRequestProto request) {

        log.info("Начат расчет getRecommendationsForUser потока рекомендованных мероприятий для указанного пользователя");
        // Для получение ограничения выборки, готовлю Pageable
        // где N это request.getMaxResults()
        // request.getMaxResults() - Long, должен быть в пределах Integer.MAX_VALUE, поэтому разумный лимит
        int maxResults = (int) Math.min(request.getMaxResults(), 30L);
        Pageable limit = PageRequest.of(0, maxResults);

        //1. Этап -  Подбор мероприятий, с которыми пользователь ещё не взаимодействовал.
        // а. Получить недавно просмотренные - Получаем последние N событий пользователя
        Set<Long> userLatestEventSet = interactionRepository.findByUserIdOrderByTsDesc(request.getUserId(),
                        limit).stream()
                .map(Interaction::getEventId)
                .collect(Collectors.toSet());
        // Если пользователь ещё не взаимодействовал ни с одним мероприятием, то рекомендовать нечего
        if (userLatestEventSet.isEmpty()) {
            log.trace("Список ID мероприятий, с которыми взаимодействовал пользователь - <<<<<<<<пуст>>>>>>>>");
            return Stream.empty();
        } else {
            log.trace("Списко ID мероприятий, с которыми взаимодействовал пользователь - {}", userLatestEventSet);
        }
        // б. Найти похожие новые - Находим все похожие события (рекомендации)
        // сортированный по убыванию похожести и ограниченный список, включающий пару мерроприятий,
        // только с одним из которых взаимодействовал пользователь,
        // а второе событие - максимально похожее, но с ним пользователь не взаимодействовал
        List<Similarity> similarityList = similarityRepository.
                findTopRelevantSimilarities(userLatestEventSet, limit).getContent();

        if (similarityList.isEmpty()) {
            log.trace("||||||||||||||||| -------- Список SIMILARITIES пуст");
            return Stream.empty();
        } else {
            log.trace("||||||||||||||||| -------- Список SIMILARITIES с парами мерроприятий - {}", similarityList);
        }

        // c. Выбрать N самых похожих.
        // Получаем ВСЕ события, с которыми пользователь уже взаимодействовал, SET даст (O(1) вместо O(n))
        Set<Long> userEventIds = interactionRepository.findByUserId(request.getUserId()).stream()
                .map(Interaction::getEventId)
                .collect(Collectors.toSet());
        // Извлекаем рекомендуемые ID мероприятий, с которыми пользователь не взаимодействовал
        Set<Long> newEventIds = similarityList.stream()
                .map(sim -> userLatestEventSet.contains(sim.getEvent1()) ?
                        sim.getEvent2() :
                        sim.getEvent1())
                .filter(id -> !userEventIds.contains(id)) // ← критически важно!
                .collect(Collectors.toSet());

        if (newEventIds.isEmpty()) {
            log.trace("||||||||||||||||| -------- Список newEventIds пуст");
            return Stream.empty();
        } else {
            log.trace("||||||||||||||||| -------- Список newEventIds  - {}", newEventIds);
        }

        log.debug("|||| ------------------------------------------------------------------------------------- ||||");
        log.trace("|||| ------ Список ID рекомендуемых для пользователя мерроприятий, для которых вычисляем оценку - {}",
                newEventIds);
        log.debug("|||| ------ Полученная длина списка - {}  ------- ||||",
                newEventIds.size());
        log.debug("|||| ------ Запрошенная длина списк - {}  ------- ||||",
                request.getMaxResults());
        log.debug("|||| ------------------------------------------------------------------------------------- ||||");


        // 2. Вычисление оценки для каждого нового мероприятия.
        // a. Найти K ближайших соседей.
        // Загружаем ВСЕ нужные Similarity за 1 запрос
        List<Similarity> allSimilarities = similarityRepository.findUserRelevantSimilarities(
                newEventIds, userEventIds
        );

        log.debug("Загружено {} записей Similarity для расчёта", allSimilarities.size());

        // Группируем мапу по newEventId
        Map<Long, List<Similarity>> similaritiesByNewEvent = allSimilarities.stream()
                .collect(Collectors.groupingBy(
                        sim -> newEventIds.contains(sim.getEvent1()) ?
                                sim.getEvent1() :
                                sim.getEvent2()
                ));

        // б. Получить оценки.
        // Загружаем ВСЕ рейтинги за 1 запрос
        Set<Long> allUserEventIds = allSimilarities.stream()
                .map(sim ->
                        newEventIds.contains(sim.getEvent1()) ? sim.getEvent2() : sim.getEvent1())
                .collect(Collectors.toSet());

        if (allUserEventIds.isEmpty()) {
            log.trace("||||||||||||||||| -------- Список allUserEventIds пуст");
            return Stream.empty();
        } else {
            log.trace("||||||||||||||||| -------- Список allUserEventIds  - {}", allUserEventIds);
        }

        // собираю мапу пользовательских оценок
        Map<Long, Double> userRatings = interactionRepository.findByEventIdIn(allUserEventIds.stream().toList())
                .stream()
                .collect(Collectors.toMap(Interaction::getEventId,
                        Interaction::getRating,
                        Double::max));


        // c. Вычисляем оценки
        Map<Long, Double> recommendations = new HashMap<>();
        for (Map.Entry<Long, List<Similarity>> entry : similaritiesByNewEvent.entrySet()) {
            Long newEventId = entry.getKey();
            double weightedSum = 0.0;
            double similaritySum = 0.0;

            for (Similarity sim : entry.getValue()) {
                Long userEventId = newEventId.equals(sim.getEvent1())
//                Long userEventId = newEventIds.contains(sim.getEvent1())
                        ? sim.getEvent2()
                        : sim.getEvent1();
                double rating = userRatings.getOrDefault(userEventId, 0.0);
                double simValue = sim.getSimilarity();

                weightedSum += simValue * rating;
                similaritySum += simValue;
            }

            double prediction = (similaritySum == 0) ? 0.0 : weightedSum / similaritySum;
            recommendations.put(newEventId, prediction);
        }

        return recommendations.entrySet().stream()
                .map(e -> toProto(e.getKey(), e.getValue()));
    }


    @Override
    public Stream<RecommendedEventProto> getSimilarEvents(SimilarEventsRequestProto request) {
        // возвращает поток мероприятий, с которыми не взаимодействовал этот пользователь,
        // но которые максимально похожи на указанное мероприятие:
        log.info("Начат расчет getSimilarEvents для поиска мероприятий, похожих на заданное");

        if (request.getMaxResults() <= 0) {
            log.trace("||||||||||||||||| -------- параметр  getMaxResults  <= 0");
            return Stream.empty();
        } else {
            log.trace("||||||||||||||||| -------- параметр  getMaxResults  - {}", request.getMaxResults());
        }

        // Для получение ограничения выборки, готовлю Pageable
        // где N это request.getMaxResults()
        // request.getMaxResults() - Long, должен быть в пределах Integer.MAX_VALUE, поэтому разумный лимит
        int maxResults = (int) Math.min(request.getMaxResults(), 10_000L);
        Pageable limit = PageRequest.of(0, maxResults);

        // Получаем события, с которыми пользователь уже взаимодействовал, SET даст (O(1) вместо O(n))
        Set<Long> userEventSet = interactionRepository.findByUserId(request.getUserId()).stream()
                .map(Interaction::getEventId)
                .collect(Collectors.toSet());

        log.trace("||||||||||||||||| -------- Список userEventSet  - {}", userEventSet);

        // Выгружаю из базы список всех similarities, которые содержат пары мерроприятий, одно из которых
        // соответсвует указанному,
        long targetEventId = request.getEventId();
        // с исключением тех,с которыми в которых пользователь взаимодействовал с обоими мероприятиями.
        // Список отсортирован по убыванию похожести и ограничен

        List<Similarity> similarityList;
        if (userEventSet.isEmpty()) {
            // Если пользователь ещё не взаимодействовал ни с одним мероприятием
            // необходимо вызвать другой метод
            // В JPQL NOT IN :emptySet генерирует SQL NOT IN (), что приводит к ошибке
            log.trace("||||||||||||||||| -------- Список userEventSet() пуст");
            similarityList = similarityRepository.findSimilarWithoutUserFilter(
                    targetEventId, limit).getContent();
        } else {
            similarityList = similarityRepository.findSimilarEvents(
                    targetEventId, userEventSet, limit).getContent();
        }

        // Собираем мапу, ключ- рекомендуемое событие, не совпадающее с заданным мероприятием
        // значение -похожесть (с разрешением конфликтов)
        Map<Long, Double> eventSimMap = similarityList.stream()
                .collect(Collectors.toMap(
                        similarity -> similarity.getEvent1() == targetEventId
                                ? similarity.getEvent2()
                                : similarity.getEvent1(),
                        Similarity::getSimilarity,
                        //обработка дубликатов - при наличии двух записей с одним и тем же рекомендуемым eventId будет:
                        //IllegalStateException: Duplicate key
                        // Стратегия разрешения похожести - максимальное значение
                        Double::max
                ));

        return eventSimMap.entrySet().stream()
                .map(entry -> toProto(entry.getKey(), entry.getValue()));
    }


    @Override
    public Stream<RecommendedEventProto> getInteractionsCount(InteractionsCountRequestProto request) {
        // получает идентификаторы мероприятий и возвращает их поток с суммой максимальных
        // весов действий каждого пользователя с этими мероприятиями:

        log.info("Начат расчет getInteractionsCount суммы максимальных весов взаимодействий пользователей с указанными мероприятиями");

        if (request.getEventIdList().isEmpty()) {
            log.trace("||||||||||||||||| -------- Список EventIdList() пуст");
            return Stream.empty();
        } else {
            log.trace("||||||||||||||||| -------- Список EventIdList()  - {}", request.getEventIdList());
        }

        // чтобы не превысить лимит параметров SQL )))
        if (request.getEventIdList().size() > 1000) {
            throw new IllegalArgumentException("Слишком много мероприятий в запросе");
        }

        // события из request.getEventIdList() которых= нет в БД в результат не войдут !!!
//        List<Interaction> interactionList = interactionRepository.findByEventIdIn(request.getEventIdList());
//        Map<Long, Double> eventRatingMap = interactionList.stream()
//                .filter(interaction -> interaction.getRating() != null)
//                .collect(Collectors.groupingBy(
//                        Interaction::getEventId,
//                        Collectors.summingDouble(Interaction::getRating)
//                ));
        // для учета всех меррпориятий из списка
        // Инициализируем мапу всеми запрошенными ID
        Map<Long, Double> eventRatingMap = request.getEventIdList().stream()
                .collect(Collectors.toMap(eventId -> eventId, eventId -> 0.0));
        // Добавляем реальные коэффициенты
        interactionRepository.findByEventIdIn(request.getEventIdList())
                .stream()
                .filter(interaction -> interaction.getRating() != null)
                .forEach(interaction ->
                        eventRatingMap.merge(interaction.getEventId(), interaction.getRating(), Double::sum)
                );
        log.trace("||||||||||||||||| --------  userEventSet eventRatingMap - {}", eventRatingMap);

        return eventRatingMap.entrySet().stream()
                .map(entry -> toProto(entry.getKey(), entry.getValue()));
    }

    // Вспомогательный метод
    private RecommendedEventProto toProto(Long eventId, Double score) {
        return RecommendedEventProto.newBuilder()
                .setEventId(eventId)
                .setScore(score != null ? score : 0.0)
                .build();
    }
}