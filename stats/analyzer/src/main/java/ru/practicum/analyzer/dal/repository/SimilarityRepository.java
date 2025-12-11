package ru.practicum.analyzer.dal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.analyzer.dal.entity.Similarity;

public interface SimilarityRepository extends JpaRepository<Similarity, Long> {
}
