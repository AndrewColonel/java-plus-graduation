package ru.practicum.analyzer.service;

import lombok.RequiredArgsConstructor;
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

@Service
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {

    private final InteractionRepository interactionRepository;
    private final SimilarityRepository similarityRepository;

    @Override
    // возвращает поток рекомендованных мероприятий для указанного пользователя:
    public Stream<RecommendedEventProto> getRecommendationsForUser(UserPredictionsRequestProto request) {
        // список взаимодействий Intereactions пользователя, преобразованный в списко мероприятий,
        // с которыми этот пользователь взаимодействовал
        List<Long> userEventList = interactionRepository.findByUserId(request.getUserId()).stream()
                .map(Interaction::getEventId)
                .toList();
        // список подобий Similarities пользователя
        // Если eventIds пустой, запрос вернёт все записи? на самом деле в большинстве БД IN () — ошибка).
        if (userEventList.isEmpty()) return Stream.empty();
        List<Similarity> similarityList = similarityRepository.findByEvent1InOrEvent2In(userEventList).stream()
                // Убираем из выдачи те коэффициенты подобия, в которых пользователь взаимодействовал с обоими мероприятиями.
                .filter(similarity -> !Objects.equals(similarity.getEvent1(), similarity.getEvent2()))
                // для дальнейшей сортировки фильтр на NULL в "похожести"
                .filter(similarity -> similarity.getSimilarity() != null)
                // сортировка по возрастанию похожести
                .sorted(Comparator.comparingDouble(Similarity::getSimilarity).reversed())
                // выбрать первые заданные N.
                .limit(request.getMaxResults())
                .toList();
        // собираю мапу из eventId,
        // тот event из пары, которого нет в списке мероприятий с которыми уже взаимоджейсвтовал прльзователь,
        // станет ключом
        // и в качестве значения - показатель похожести для этого мерпориятий
        Map<Long, Double> eventRatingMap = similarityList.stream()
                .collect(Collectors.toMap(
                        similarity ->
                                userEventList.contains(similarity.getEvent1())
                                        ? similarity.getEvent2() : similarity.getEvent1()
                        , Similarity::getSimilarity
                ));
        return eventRatingMap.entrySet().stream()
                .map(entry -> toProto(entry.getKey(), entry.getValue()));
    }

    @Override
    public Stream<RecommendedEventProto> getSimilarEvents(SimilarEventsRequestProto request) {
        // возвращает поток мероприятий, с которыми не взаимодействовал этот пользователь,
        // но которые максимально похожи на указанное мероприятие:
        // TODO


        return Stream.empty();
    }

    @Override
    public Stream<RecommendedEventProto> getInteractionsCount(InteractionsCountRequestProto request) {
        // получает идентификаторы мероприятий и возвращает их поток с суммой максимальных
        // весов действий каждого пользователя с этими мероприятиями:
        List<Interaction> interactionList = interactionRepository.findByEventIdIn(request.getEventIdList());
        Map<Long, Double> eventRatingMap = interactionList.stream()
                .filter(interaction -> interaction.getRating() != null)
                .collect(Collectors.groupingBy(
                        Interaction::getEventId,
                        Collectors.summingDouble(Interaction::getRating)
                ));
        return eventRatingMap.entrySet().stream()
                .map(entry -> toProto(entry.getKey(), entry.getValue()));
    }

    private RecommendedEventProto toProto(Long eventId, Double score) {
        return RecommendedEventProto.newBuilder()
                .setEventId(eventId)
                .setScore(score != null ? score : 0.0)
                .build();
    }
}