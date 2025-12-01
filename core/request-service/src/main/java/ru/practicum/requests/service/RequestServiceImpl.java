package ru.practicum.requests.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.event.EventRepository;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.State;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.EntityNotExistsException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.requests.repository.RequestRepository;
import ru.practicum.requests.dto.RequestDto;
import ru.practicum.requests.model.entity.Request;
import ru.practicum.requests.model.RequestMapper;
import ru.practicum.user.model.entity.User;
import ru.practicum.user.repository.UserRepository;


import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class RequestServiceImpl implements RequestService {

    private final UserRepository userRepository;
    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;

    /**
     * Получение информации о заявках текущего пользователя на участие в чужих событиях
     *
     * @param userId Код пользователя
     * @return List<RequestDto> Список событий, с которых участвует пользователь
     */
    @Override
    public List<RequestDto> getUserRequests(Long userId) {
        log.warn("getUserRequests(Long {})", userId);
        // Проверить пользователя
        validateUserExists(userId);
        List<RequestDto> dtoList = RequestMapper.toDto(requestRepository.findAllByRequesterId(userId));
        return dtoList;
    }

    /**
     * Добавление запроса от текущего пользователя на участие в событии
     * Шаги:
     * 1. Проверить существование пользователя.
     * 2. Проверить существование события.
     * 3. нельзя участвовать в неопубликованном событии (Ожидается код ошибки 409)
     * 4. инициатор события не может добавить запрос на участие в своём событии (Ожидается код ошибки 409)
     * 5. если у события достигнут лимит запросов на участие - необходимо вернуть ошибку (Ожидается код ошибки 409)
     * 6. нельзя добавить повторный запрос (Ожидается код ошибки 409)
     * 7. если для события отключена пре-модерация запросов на участие,
     * то запрос должен автоматически перейти в состояние подтвержденного
     *
     * @param userId  Код пользователя
     * @param eventId Код события
     * @return RequestDto Созданный запрос
     */
    @Override
    public RequestDto addParticipationRequest(Long userId, Long eventId) {
        log.warn("addParticipationRequest(Long {}, Long {})", userId, eventId);
        // Проверить существование пользователя.
        User existedUser = validateUserExists(userId);
        // Проверить существование события.
        Event existedEvent = validateEventExists(eventId);

        // Проверить, что событие опубликовано (PUBLISHED).
        if (!existedEvent.getState().equals(State.PUBLISHED)) {
            throw new ConflictException("Событие не опубликовано");
        }

        // Проверить, что пользователь не инициатор события.
        if (existedEvent.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Пользователь является инициатором события.");
        }

        // Проверка лимита участников
        if (existedEvent.getParticipantLimit() > 0 &&
                requestRepository.countByEventIdAndStatus(eventId, Request.RequestStatus.CONFIRMED) >=
                        existedEvent.getParticipantLimit()) {
            throw new ConflictException("Превышен лимит участников события");
        }

        // Проверить, что запрос не существует (дублирование).
        if (requestRepository.findByRequesterIdAndEventId(userId, eventId).isPresent()) {
            throw new ConflictException("Данный запрос уже существует.");
        }

        Request newRequest = new Request();
        newRequest.setRequester(existedUser);
        newRequest.setEvent(existedEvent);
        newRequest.setCreated(LocalDateTime.now());


        // Если пре-модерация отключена, автоматически подтверждаем
        if (!existedEvent.getRequestModeration() || existedEvent.getParticipantLimit() == 0) {
            newRequest.confirm();
        }

        requestRepository.save(newRequest);
        RequestDto dto = RequestMapper.toDto(newRequest);

        return dto;
    }

    /**
     * Отмена своего запроса на участие в событии
     * Проверки:
     * 1. Пользователь существует.
     * 2. Заявка существует.
     * 3. Заявка принадлежит пользователю.
     * 4. Заявка должна быть в состоянии PENDING.
     *
     * @param userId    Код пользователя
     * @param requestId Код запроса
     * @return Отмененный запрос
     */
    @Override
    public RequestDto cancelRequest(Long userId, Long requestId) {
        log.warn("cancelRequest(Long {}, Long {})", userId, requestId);
        // Проверить пользователя
        validateUserExists(userId);
        // Проверить запрос
        Request existedRequest = validateRequestExists(requestId);

        // Проверить, что запрос принадлежит пользователю
        if (!existedRequest.getRequester().getId().equals(userId)) {
            throw new NotFoundException("Запрос не принадлежит пользователю.");
        }

        // Изменение статуса на RequestStatus.CANCELED
        existedRequest.cancel();

        RequestDto dto = RequestMapper.toDto(existedRequest);
        return dto;
    }

    /**
     * Проверка переданного в поиск ID запроса
     *
     * @param requestId ID запроса
     * @return Если существует, возвращается Request
     */
    private Request validateRequestExists(Long requestId) {
        log.warn("validateRequestExists(Long {})", requestId);
        // Проверка на null ID
        if (requestId == null) {
            throw new IllegalArgumentException("ID запроса не может быть null");
        }

        // Проверка существования подборки
        return requestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotExistsException("Запрос с ID " + requestId + " не найден"));
    }


    /**
     * Проверка переданного в поиск ID события
     *
     * @param eventId ID запроса
     * @return Если существует, возвращается Service
     */
    private Event validateEventExists(Long eventId) {
        log.warn("validateEventExists(Long {})", eventId);
        // Проверка на null ID
        if (eventId == null) {
            throw new IllegalArgumentException("ID запроса не может быть null");
        }

        // Проверка существования подборки
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotExistsException("Запрос с ID " + eventId + " не найден"));
    }

    /**
     * Проверка переданного в поиск ID пользователя
     *
     * @param userId ID пользователя
     * @return Если существует, возвращается User
     */
    private User validateUserExists(Long userId) {
        log.warn("validateUserExists(Long {})", userId);
        // Проверка на null ID
        if (userId == null) {
            throw new IllegalArgumentException("ID пользователя не может быть null");
        }

        // Проверка существования подборки
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotExistsException("Пользователь с ID " + userId + " не найден"));
    }

}
