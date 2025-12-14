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

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {

    private final InteractionRepository interactionRepository;
    private final SimilarityRepository similarityRepository;


    // Ограничем выборку пользовательских мероприятий для расчета предсказаний оценки
    private static int USER_LIMIT = 10;


    @Override
    // возвращает поток рекомендованных мероприятий для указанного пользователя:
    public Stream<RecommendedEventProto> getRecommendationsForUser(UserPredictionsRequestProto request) {

        log.info("Начат расчет getRecommendationsForUser потока рекомендованных мероприятий для указанного пользователя");
        // Для получение ограничения выборки, готовлю Pageable
        // где N это request.getMaxResults()
        // request.getMaxResults() - Long, должен быть в пределах Integer.MAX_VALUE, поэтому разумный лимит
        int maxResults = (int) Math.min(request.getMaxResults(), 30L);
        Pageable limit = PageRequest.of(0, maxResults);

        // Получаем события, с которыми пользователь уже взаимодействовал, SET даст (O(1) вместо O(n))
        // Списко взаимодейсвтий пользователя, отсортированный по по дате и ограниченный MaxResults количестом
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

//        // Список ID мероприятий, похожие на те, что отобрали на предыдущем этапе,
//        // но при этом пользователь с ними не взаимодействовал.
//        List<Long> newEvenetList = similarityList.stream()
//                .map(similarity -> userLatestEventSet.contains(similarity.getEvent1()) ?
//                        similarity.getEvent2() : similarity.getEvent1())
//                .toList();

        // Мапа, в которой ключи - это  ID мероприятий, похожие на те, что отобрали на предыдущем этапе,
        // но при этом пользователь с ними не взаимодействовал.
        // значения заполняю 0, чтобы потом поместить туда предсказанную оценку
        Map<Long, Double> recommendationMap = similarityRepository.
                findTopRelevantSimilarities(userLatestEventSet, limit).stream()
                .collect(Collectors.toMap(
                        similarity -> userLatestEventSet.contains(similarity.getEvent1()) ?
                                similarity.getEvent2() : similarity.getEvent1(),
                        similarity -> 0.0
                ));


        log.debug("|||| ------------------------------------------------------------------------------------- ||||");
        log.trace("|||| ------ Список ID новый для пользователя мерроприятий, для которых вычисляем оценку - {}",
                recommendationMap.keySet());
        log.debug("|||| ------ Полученная длина списка - {}  ------- ||||",
                recommendationMap.keySet().size());
        log.debug("|||| ------ Запрошенная длина списк - {}  ------- ||||",
                request.getMaxResults());
        log.debug("|||| ------------------------------------------------------------------------------------- ||||");

        // Получаем события, с которыми пользователь уже взаимодействовал, SET даст (O(1) вместо O(n))
        Set<Long> userEventSet = interactionRepository.findByUserId(request.getUserId()).stream()
                .map(Interaction::getEventId)
                .collect(Collectors.toSet());


        // вычисляем оценку для нового, незнакомого пользователю мероприятия
        // Ограничем выборку пользовательских мероприятий для расчете USER_LIMIT мероприятиями

        // размер данного цикла - ограничен и сравнительно мал, позволительно сделать запросы к базе в цикле?
        for (Long newEventId : recommendationMap.keySet()) {
            // нашли  USER_LIMIT мероприятий пользователя, максимально похожие на newEventId
            // собрал мапу - userEventId -> sim
            Map<Long, Double> userEventSimMap =
                    similarityRepository.findUserRelevantSimilarities(
                                    newEventId,
                                    userEventSet,
                                    PageRequest.of(0, USER_LIMIT)).stream()
                            .collect(Collectors.toMap(
                                    similarity -> Objects.equals(similarity.getEvent1(), newEventId)
                                            ? similarity.getEvent2()
                                            : similarity.getEvent1(),
                                    Similarity::getSimilarity,
                                    //обработка дубликатов - при наличии двух записей с одним и тем же рекомендуемым eventId будет:
                                    //IllegalStateException: Duplicate key
                                    // Стратегия разрешения похожести - максимальное значение
                                    Double::max
                            ));
            // Получил оценки для выбранных мероприятий
            // собрал мапу - userEventId -> rate
            Map<Long, Double> userEvenetRateMap =
                    interactionRepository.findByEventIdIn(userEventSimMap.keySet().stream().toList()).stream()
                            .collect(Collectors.toMap(
                                    Interaction::getEventId,
                                    Interaction::getRating
                            ));

            //Вычислить сумму взвешенных оценок
            double sumOfWeightedEstimates = 0.0;
            for (Map.Entry<Long, Double> UserEventSimEntry : userEventSimMap.entrySet()) {
                sumOfWeightedEstimates = sumOfWeightedEstimates
                        + UserEventSimEntry.getValue()
                                * userEvenetRateMap.getOrDefault(UserEventSimEntry.getKey(),0.0);
            }

            // Вычислить сумму коэффициентов подобия
            double sumOfSimilarityCoefficients = 0.0;
            for (Double sim : userEventSimMap.values()) {
                sumOfSimilarityCoefficients = sumOfSimilarityCoefficients + sim;
            }

            // Вычислить оценку нового мероприятия
            double evaluationRatingOfNewEvent = sumOfWeightedEstimates / sumOfSimilarityCoefficients;

            recommendationMap.put(newEventId,evaluationRatingOfNewEvent);
        }


        return recommendationMap.entrySet().stream()
                .map(entry -> toProto(entry.getKey(), entry.getValue()));
    }


    @Override
    public Stream<RecommendedEventProto> getSimilarEvents(SimilarEventsRequestProto request) {
        // возвращает поток мероприятий, с которыми не взаимодействовал этот пользователь,
        // но которые максимально похожи на указанное мероприятие:
        if (request.getMaxResults() <= 0) return Stream.empty();

        // Для получение ограничения выборки, готовлю Pageable
        // где N это request.getMaxResults()
        // request.getMaxResults() - Long, должен быть в пределах Integer.MAX_VALUE, поэтому разумный лимит
        int maxResults = (int) Math.min(request.getMaxResults(), 10_000L);
        Pageable limit = PageRequest.of(0, maxResults);

        // Получаем события, с которыми пользователь уже взаимодействовал, SET даст (O(1) вместо O(n))
        Set<Long> userEventSet = interactionRepository.findByUserId(request.getUserId()).stream()
                .map(Interaction::getEventId)
                .collect(Collectors.toSet());

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
        if (request.getEventIdList().isEmpty()) return Stream.empty();
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