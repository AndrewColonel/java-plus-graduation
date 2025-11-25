package ru.practicum.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.server.model.EndpointHit;
import ru.practicum.server.model.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

public interface EndpointHitRepository extends JpaRepository<EndpointHit, Long> {

    // получаем сущность ViewStats, отличную от сущности репозитория, ипользуя метод
    // setResultTransformer интерфейса org.hibernate.query.Query

    // методы для подсчета без учета уникальных IP адресов
    @Query("select new ru.practicum.server.model.ViewStats(e.app, e.uri, count(e.ip)) " +
            "from EndpointHit as e " +
            "where e.uri in ?3 " +
            "and e.timestamp between ?1 and ?2 " +
            "group by e.app, e.uri " +
            "order by count(e.ip) desc")
    List<ViewStats> countHitsByIp(LocalDateTime start, LocalDateTime end, List<String> uris);

    // метод учитывает отсутсвие списка uris
    @Query("select new ru.practicum.server.model.ViewStats(e.app, e.uri, count(e.ip)) " +
            "from EndpointHit as e " +
            "where e.timestamp between ?1 and ?2 " +
            "group by e.app, e.uri " +
            "order by count(e.ip) desc")
    List<ViewStats> countHitsByIp(LocalDateTime start, LocalDateTime end);


    // методы для подсчета с учетом уникальности IP адресов
    @Query("select new ru.practicum.server.model.ViewStats(e.app, e.uri, count(distinct(e.ip))) " +
            "from EndpointHit as e " +
            "where e.uri in ?3 " +
            "and e.timestamp between ?1 and ?2 " +
            "group by e.app, e.uri " +
            "order by count(e.ip) desc")
    List<ViewStats> countIniqueHitsByIp(LocalDateTime start, LocalDateTime end, List<String> uris);

    // метод учитывает отсутсвие списка uris
    @Query("select new ru.practicum.server.model.ViewStats(e.app, e.uri, count(distinct(e.ip))) " +
            "from EndpointHit as e " +
            "where e.timestamp between ?1 and ?2 " +
            "group by e.app, e.uri " +
            "order by count(e.ip) desc")
    List<ViewStats> countIniqueHitsByIp(LocalDateTime start, LocalDateTime end);

}
