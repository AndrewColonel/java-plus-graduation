package ru.practicum.analyzer.controller;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.practicum.analyzer.service.RecommendationService;
import ru.practicum.ewm.stats.proto.RecommendationsControllerGrpc;
import ru.practicum.ewm.stats.proto.RecommendedEventProto;
import ru.practicum.ewm.stats.proto.SimilarEventsRequestProto;
import ru.practicum.ewm.stats.proto.UserPredictionsRequestProto;
import ru.practicum.ewm.stats.proto.InteractionsCountRequestProto;

@GrpcService
@Slf4j
@AllArgsConstructor
public class RecommendationsController extends RecommendationsControllerGrpc.RecommendationsControllerImplBase {

    private final RecommendationService recommendationService;

    @Override
    public void getRecommendationsForUser(
            UserPredictionsRequestProto request,
            StreamObserver<RecommendedEventProto> responseObserver) {
        try {
            log.debug("Получен запрос на рекомендованные мероприятия для пользователя: userId={}", request.getUserId());
            // возвращает Stream<RecommendedEventProto>
            recommendationService.getRecommendationsForUser(request)
                    .forEach(responseObserver::onNext);
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("Ошибка при получении рекомендаций для пользователя", e);
            responseObserver.onError(new StatusRuntimeException(
                    Status.INTERNAL
                            .withDescription("Не удалось получить рекомендации")
                            .withCause(e)));
        }
    }

    @Override
    public void getSimilarEvents(
            SimilarEventsRequestProto request,
            StreamObserver<RecommendedEventProto> responseObserver) {
        try {
            log.debug("Получен запрос на макисмально похожие на мероприятие: eventId={}", request.getEventId());
            // возвращает Stream<RecommendedEventProto>
            recommendationService.getSimilarEvents(request)
                    .forEach(responseObserver::onNext);
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("Ошибка при поиске похожих событий", e);
            responseObserver.onError(new StatusRuntimeException(
                    Status.INTERNAL
                            .withDescription("Не удалось найти похожие события")
                            .withCause(e)));
        }
    }

    @Override
    public void getInteractionsCount(
            InteractionsCountRequestProto request,
            StreamObserver<RecommendedEventProto> responseObserver) {
        try {
            log.debug("Получен запрос на подсчёт максимальных весов для списка мероприятий: List<eventId>={}",
                    request.getEventIdList());
            // возвращает Stream<RecommendedEventProto>
            recommendationService.getInteractionsCount(request)
                    .forEach(responseObserver::onNext);
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("Ошибка при подсчёте взаимодействий", e);
            responseObserver.onError(new StatusRuntimeException(
                    Status.INTERNAL
                            .withDescription("Не удалось подсчитать взаимодействия")
                            .withCause(e)));
        }
    }
}