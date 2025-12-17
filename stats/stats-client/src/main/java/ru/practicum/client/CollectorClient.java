package ru.practicum.client;

import com.google.protobuf.Empty;
import com.google.protobuf.Timestamp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.proto.ActionTypeProto;
import ru.practicum.ewm.stats.proto.UserActionControllerGrpc;
import ru.practicum.ewm.stats.proto.UserActionProto;

import java.time.Instant;


@Slf4j
@Component
@RequiredArgsConstructor
public class CollectorClient {

    @GrpcClient("collector")
    private final UserActionControllerGrpc.UserActionControllerBlockingStub client;

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

        log.trace("Отправляю данные: {}", request.getAllFields());
        Empty response = client.collectUserAction(request);
        log.trace("Получил ответ от коллектора: {}", response);
    }

}
