package ru.practicum.analyzer.dal.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.analyzer.dal.entity.Similarity;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface SimilarityRepository extends JpaRepository<Similarity, Long> {

    Optional<Similarity> findByEvent1AndEvent2(Long event1, Long event2);

    // метод для постобработки данных в потоке
    @Query("SELECT s FROM Similarity s WHERE s.event1 IN :eventIds OR s.event2 IN :eventIds")
    List<Similarity> findByEvent1InOrEvent2In(@Param("eventIds") Set<Long> eventIds);

    // метод выгружает похожести, соотвесвтующие списку и убирает те коэфициенты,
    //  в которых пользователь взаимодействовал с обоими мероприятиями.
    // сортировка по убыванию значению коэффициента

    @Query(value = "SELECT * FROM similarity s " +
            "WHERE (s.event1 IN :eventIds OR s.event2 IN :eventIds) " +
            // XOR: ровно одно событие из пары принадлежит пользователю
            "AND (s.event1 IN :eventIds) <> (s.event2 IN :eventIds) " +
            "ORDER BY s.similarity DESC",
            nativeQuery = true)
    List<Similarity> findTopRelevantSimilarities(@Param("eventIds") Set<Long> eventIds, Pageable pageable);



    // Убрать из выдачи те коэффициенты подобия,
    // в которых пользователь взаимодействовал с обоими мероприятиями.
    @Query("SELECT s FROM Similarity s " +
            "WHERE (s.event1 = :eventId OR s.event2 = :eventId) " +
            "  AND ( " +
            "    (s.event1 = :eventId AND s.event2 NOT IN :userEventIds) " +
            "    OR " +
            "    (s.event2 = :eventId AND s.event1 NOT IN :userEventIds) " +
            "  ) " +
            "ORDER BY s.similarity DESC")
    Page<Similarity> findSimilarEvents(
            @Param("eventId") Long eventId,
            @Param("userEventIds") Set<Long> userEventIds,
            Pageable pageable
    );

    // без фильтра по списку пользовательских мероприятий,
    // в условиях, когда такой список пуст
    @Query("SELECT s FROM Similarity s " +
            "WHERE s.event1 = :eventId OR s.event2 = :eventId " +
            "ORDER BY s.similarity DESC")
    Page<Similarity> findSimilarWithoutUserFilter(
            @Param("eventId") Long eventId,
            Pageable pageable);


}
