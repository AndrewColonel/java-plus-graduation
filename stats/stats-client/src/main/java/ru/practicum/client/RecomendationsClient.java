package ru.practicum.client;

import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.proto.*;

import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Slf4j
@Component
public class RecomendationsClient {

    private final RecommendationsControllerGrpc.RecommendationsControllerBlockingStub client;

    public RecomendationsClient(@GrpcClient("analyzer")
                                RecommendationsControllerGrpc.RecommendationsControllerBlockingStub client) {
        this.client = client;
    }

    public Stream<RecommendedEventProto> getRecommendationsForUser(long userId, int maxResults) {
        UserPredictionsRequestProto request = UserPredictionsRequestProto.newBuilder()
                .setUserId(userId)
                .setMaxResults(maxResults)
                .build();

        try {
            log.debug("Запрос получения рекоментдаций для пользователя userId={}, maxResult={}",
                    userId, maxResults);
            Iterator<RecommendedEventProto> iterator = client.getRecommendationsForUser(request);
            return asStream(iterator);
        } catch (
                StatusRuntimeException e) {
            log.error("Ошибка gRPC при вызове getRecommendationsForUser для userId={}", userId, e);
            return Stream.empty();
        }
    }

    public Stream<RecommendedEventProto> getSimilarEvents(long eventId, long userId, int maxResults) {
        SimilarEventsRequestProto request = SimilarEventsRequestProto.newBuilder()
                .setEventId(eventId)
                .setUserId(userId)
                .setMaxResults(maxResults)
                .build();

        // gRPC-метод getSimilarEvents возвращает Iterator, потому что в его схеме
        // мы указали, что он должен вернуть поток сообщений (stream stats.message.RecommendedEventProto)
        try {
            log.debug("запрос на макисмально похожие на мероприятие: eventId={}", eventId);
            Iterator<RecommendedEventProto> iterator = client.getSimilarEvents(request);
            // преобразуем Iterator в Stream
            return asStream(iterator);
        } catch (
                StatusRuntimeException e) {
            log.error("Ошибка gRPC при запрос на макисмально похожие на мероприятие: eventId={}", eventId, e);
            return Stream.empty();
        }
    }

    public Stream<RecommendedEventProto> getInteractionsCount(List<Long> eventIds) {
        InteractionsCountRequestProto request = InteractionsCountRequestProto.newBuilder()
                .addAllEventId(eventIds)
                .build();
        try {
            log.debug("запрос на подсчёт максимальных весов для списка мероприятий: List<eventId>={}", eventIds);
            Iterator<RecommendedEventProto> iterator = client.getInteractionsCount(request);
            return asStream(iterator);
        } catch (
                StatusRuntimeException e) {
            log.error("Ошибка gRPC при запрос на подсчёт максимальных весов для списка мероприятий: List<eventId>={}",
                    eventIds, e);
            return Stream.empty();
        }
    }


    private Stream<RecommendedEventProto> asStream(Iterator<RecommendedEventProto> iterator) {
        return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED),
                false
        );
    }

}




