package ru.practicum.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import ru.practicum.compilations.dto.EndpointHitDto;
import ru.practicum.compilations.dto.ViewStatsDto;

import java.util.List;

@Component
public class StatsClient {
    private final RestClient restClient;

    public StatsClient(@Value("${stats-server.url}") String baseUrl) {
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
    }

    /**
     * Сохраняет информацию о запросе к эндпоинту (POST /hit)
     *
     * @param hit Входящий EndpointHitDto
     */
    public void saveHit(EndpointHitDto hit) {
        restClient.post()
                .uri("/hit")
                .body(hit)
                .retrieve()
                .toBodilessEntity();
    }


    /**
     * Получает статистику по посещениям (GET /stats)
     *
     * @param start  Дата и время начала диапазона за который нужно выгрузить статистику
     * @param end    Дата и время конца диапазона за который нужно выгрузить статистику
     * @param uris   Список uri для которых нужно выгрузить статистику
     * @param unique Нужно ли учитывать только уникальные посещения (только с уникальным ip)
     * @return List<ViewStatsDto> Список посещений
     */
    public List<ViewStatsDto> getStats(String start, String end, List<String> uris, Boolean unique) {
        ViewStatsDto[] statsArray = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/stats")
                        .queryParam("start", start)
                        .queryParam("end", end)
                        .queryParam("uris", uris)
                        .queryParam("unique", unique)
                        .build())
                .retrieve()
                .body(ViewStatsDto[].class);

        return statsArray != null ? List.of(statsArray) : List.of();
    }
}