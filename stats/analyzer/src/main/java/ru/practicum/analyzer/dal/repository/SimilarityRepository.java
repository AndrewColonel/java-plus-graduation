package ru.practicum.analyzer.dal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.analyzer.dal.entity.Similarity;

import java.util.List;
import java.util.Optional;

public interface SimilarityRepository extends JpaRepository<Similarity, Long> {

    Optional<Similarity> findByEvent1AndEvent2(Long event1, Long event2);

    @Query("SELECT s FROM Similarity s WHERE s.event1 IN :eventIds OR s.event2 IN :eventIds")
    List<Similarity> findByEvent1InOrEvent2In(@Param("eventIds") List<Long> eventIds);
}
