package ru.practicum.analyzer.dal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.analyzer.dal.entity.Interaction;

import java.util.Optional;
import java.util.List;


public interface InteractionRepository extends JpaRepository<Interaction, Long> {

    Optional<Interaction> findByUserIdAndEventId(Long userId, Long eventId);

    List<Interaction> findByEventIdIn(List<Long> eventId);

    List<Interaction> findByUserId(Long userId);
}
