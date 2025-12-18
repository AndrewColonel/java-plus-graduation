package ru.practicum.client;

import com.google.protobuf.Empty;
import com.google.protobuf.Timestamp;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.proto.ActionTypeProto;
import ru.practicum.ewm.stats.proto.UserActionControllerGrpc;
import ru.practicum.ewm.stats.proto.UserActionProto;

import java.time.Instant;

@Slf4j
@Component
public class CollectorClient {

    private final UserActionControllerGrpc.UserActionControllerBlockingStub client;

    public CollectorClient(@GrpcClient("collector")
                           UserActionControllerGrpc.UserActionControllerBlockingStub client) {
        this.client = client;
    }

    public void collectUserAction(Long userId, Long eventId, String actionType) {

        UserActionProto request = UserActionProto.newBuilder()
                .setUserId(userId)
                .setEventId(eventId)
                .setActionType(ActionTypeProto.valueOf(actionType))
                .setTimestamp(Timestamp.newBuilder()
                        .setSeconds(Instant.now().getEpochSecond())
                        .setNanos(Instant.now().getNano())
                        .build())
                .build();

        try {
            log.debug("Запрос на взаимодействие userId={} с eventID={}, тип ActionType={}",
                    userId, eventId, actionType);
            log.trace("Отправляю данные: {}", request.getAllFields());
            Empty response = client.collectUserAction(request);
            log.trace("Получил ответ от коллектора: {}", response);
        } catch (StatusRuntimeException e) {
            log.error("Ошибка gRPC при вызове CollectUserAction с типом {}", actionType, e);
        }
    }
}
