package ru.practicum.analyzer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.analyzer.dal.repository.InteractionRepository;
import ru.practicum.analyzer.dal.repository.SimilarityRepository;
import ru.practicum.ewm.stats.proto.InteractionsCountRequestProto;
import ru.practicum.ewm.stats.proto.RecommendedEventProto;
import ru.practicum.ewm.stats.proto.SimilarEventsRequestProto;
import ru.practicum.ewm.stats.proto.UserPredictionsRequestProto;

import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {

    private final InteractionRepository interactionRepository;
    private final SimilarityRepository similarityRepository;

    @Override
    public Stream<RecommendedEventProto> getRecommendationsForUser(UserPredictionsRequestProto request) {
        // возвращает поток рекомендованных мероприятий для указанного пользователя:
//        return interactionRepository.findTopByCategory(request.getCategory(), 10)
//                .stream()
//                .map(this::toProto);

        return Stream.empty();
    }

    @Override
    public Stream<RecommendedEventProto> getSimilarEvents(SimilarEventsRequestProto request) {
        // возвращает поток мероприятий, с которыми не взаимодействовал этот пользователь,
        // но которые максимально похожи на указанное мероприятие:
//        return interactionRepository.findSimilarTo(request.getEventId(), request.getLimit())
//                .stream()
//                .map(this::toProto);


        return Stream.empty();
    }

    @Override
    public Stream<RecommendedEventProto> getInteractionsCount(InteractionsCountRequestProto request) {
        // получает идентификаторы мероприятий и возвращает их поток с суммой максимальных
        // весов действий каждого пользователя с этими мероприятиями:
//        return interactionRepository.findTopByInteractions(
//                        request.getUserId(),
//                        request.getStartDate(),
//                        request.getEndDate()
//                ).stream()
//                .map(this::toProtoWithStats);

        return Stream.empty();

    }

//    private RecommendedEventProto toProto(Event event) {
//        return RecommendedEventProto.newBuilder()
//                .setEventId(event.getId())
//                .setTitle(event.getTitle())
//                // ... другие поля
//                .build();
//    }
}