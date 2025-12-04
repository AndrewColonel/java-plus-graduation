package ru.practicum.requests.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import ru.practicum.requests.model.RequestStatus;
import ru.practicum.requests.model.entity.Request;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {

//    @Query("SELECT r FROM Request r " +
//            "JOIN FETCH r.event " +
//            "JOIN FETCH r.requester " +
//            "WHERE r.event.id IN :eventIds AND r.status = :status")
    List<Request> findByEventIdInAndStatus(@Param("eventIds") List<Long> eventIds,
                                           @Param("status") RequestStatus status);

//    @Query("SELECT r FROM Request r " +
//            "JOIN FETCH r.event " +
//            "JOIN FETCH r.requester " +
//            "WHERE r.requester.id = :userId")
    List<Request> findAllByRequesterId(@Param("userId") Long userId);

//    @Query("SELECT r FROM Request r " +
//            "JOIN FETCH r.event " +
//            "JOIN FETCH r.requester " +
//            "WHERE r.event.id = :eventId")
    List<Request> findAllByEventId(@Param("eventId") Long eventId);

//    @Query("SELECT r FROM Request r " +
//            "JOIN FETCH r.event " +
//            "JOIN FETCH r.requester " +
//            "WHERE r.id IN :requestIds")
    List<Request> findAllByIdIn(@Param("requestIds") List<Long> requestIds);

//    @Query("SELECT r FROM Request r " +
//            "JOIN FETCH r.event " +
//            "JOIN FETCH r.requester " +
//            "WHERE r.requester.id = :userId AND r.event.id = :eventId")
    Optional<Request> findByRequesterIdAndEventId(
            @Param("userId") Long userId,
            @Param("eventId") Long eventId);

//    @Query("SELECT COUNT(r) FROM Request r WHERE r.event.id = :eventId AND r.status = :status")
    Long countByEventIdAndStatus(@Param("eventId") Long eventId,
                                 @Param("status") RequestStatus status);

}
