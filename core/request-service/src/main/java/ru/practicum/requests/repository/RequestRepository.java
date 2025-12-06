package ru.practicum.requests.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.requests.model.RequestStatus;
import ru.practicum.requests.model.entity.Request;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> findByEventIdInAndStatus(List<Long> eventIds, RequestStatus status);

    List<Request> findAllByRequesterId(Long userId);

    List<Request> findAllByEventId(Long eventId);

    List<Request> findAllByIdIn(List<Long> requestIds);

    Optional<Request> findByRequesterIdAndEventId(Long userId, Long eventId);

    Long countByEventIdAndStatus(Long eventId, RequestStatus status);

}
