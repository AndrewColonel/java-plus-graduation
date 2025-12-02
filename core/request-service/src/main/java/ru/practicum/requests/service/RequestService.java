package ru.practicum.requests.service;

import ru.practicum.requests.dto.RequestDto;
import ru.practicum.requests.model.entity.Request;

import java.util.List;

public interface RequestService {

    /**
     * Получение информации о заявках текущего пользователя на участие в чужих событиях
     *
     * @param userId Код пользователя
     * @return List<RequestDto> Список событий, с которых участвует пользователь
     */
    List<RequestDto> getUserRequests(Long userId);

    /**
     * Добавление запроса от текущего пользователя на участие в событии
     *
     * @param userId  Код пользователя
     * @param eventId Код события
     * @return RequestDto Созданный запрос
     */
    RequestDto addParticipationRequest(Long userId, Long eventId);

    /**
     * Отмена своего запроса на участие в событии
     *
     * @param userId    Код пользователя
     * @param requestId Код запроса
     * @return Отмененный запрос
     */
    RequestDto cancelRequest(Long userId, Long requestId);

    List<RequestDto> getEventRequests(Long eventId);

    List<RequestDto> findAllRequests(List<Long> requestIds);

    Long countRequest(Long eventId, Request.RequestStatus status);

    List<RequestDto> saveAllRequests(List<RequestDto> requestDtoList);

    List<RequestDto> findRequestByStatus(List<Long> requestId,Request.RequestStatus status);

}
