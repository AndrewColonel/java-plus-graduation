package ru.practicum.analyzer.service;

import lombok.RequiredArgsConstructor;
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
import java.util.Set;
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

        // Получаем события, с которыми пользователь уже взаимодействовал, SET даст (O(1) вместо O(n))
        Set<Long> userEventSet = interactionRepository.findByUserId(request.getUserId()).stream()
                .map(Interaction::getEventId)
                .collect(Collectors.toSet());

        // Если пользователь ни с чем не взаимодействовал — возвращаем пустой поток
        if (userEventSet.isEmpty()) return Stream.empty();

        // список подобий Similarities пользователя - релевантные коэффициенты подобия (уже отсортированы по убыванию)
        // ровно одно событие из пары принадлежит пользователю
        // Ограничиваем количество результатов
        Pageable limit = PageRequest.of(0, Math.toIntExact(request.getMaxResults()));
        // request.getMaxResults() в пределах Integer.MAX_VALUE

        List<Similarity> similarityList = similarityRepository.
                findTopRelevantSimilarities(userEventSet, limit);

        // Собираем мапу, ключ- рекомендуемое событие, значение -похожесть (с разрешением конфликтов)
        Map<Long, Double> eventRatingMap = similarityList.stream()
                .collect(Collectors.toMap(
                        similarity -> userEventSet.contains(similarity.getEvent1())
                                ? similarity.getEvent2()
                                : similarity.getEvent1(),
                        Similarity::getSimilarity,
                        //обработка дубликатов - при наличии двух записей с одним и тем же рекомендуемым eventId будет:
                        //IllegalStateException: Duplicate key
                        // Стратегия разрешения похожести - максимальное значение
                        Double::max
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
                .collect(Collectors.toMap(id -> id, id -> 0.0));
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