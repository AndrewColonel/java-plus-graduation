package ru.practicum.event.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.practicum.event.model.entity.Event;
import ru.practicum.event.model.State;


import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {
    Page<Event> findAllByInitiatorIdOrderByCreatedOnDesc(Long initiatorId, Pageable pageable);

    Optional<Event> findEventByIdAndState(Long id, State state);

    List<Event> findAllByIdIn(List<Long> id);
}
