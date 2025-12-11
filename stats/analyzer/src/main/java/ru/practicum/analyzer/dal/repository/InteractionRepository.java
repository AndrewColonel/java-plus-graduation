package ru.practicum.analyzer.dal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.analyzer.dal.entity.Interaction;

public interface InteractionRepository extends JpaRepository<Interaction, Long> {
}
