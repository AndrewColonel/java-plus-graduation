package ru.practicum.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.MaxAttemptsRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import ru.practicum.compilations.dto.EndpointHitDto;
import ru.practicum.compilations.dto.ViewStatsDto;

import java.util.List;

@Component
public class StatsClient {
    private final DiscoveryClient discoveryClient;
    private final RetryTemplate retryTemplate;
    private final String statsServiceId;

    public StatsClient(@Value("${discovery.services.stats-server-id}") String statsServiceId,
                       DiscoveryClient discoveryClient) {
        this.statsServiceId = statsServiceId;
        this.discoveryClient = discoveryClient;
        this.retryTemplate = new RetryTemplate();

        RetryTemplate retryTemplate = new RetryTemplate();

        FixedBackOffPolicy fixedBackOffPolicy = new FixedBackOffPolicy();
        fixedBackOffPolicy.setBackOffPeriod(5000L);
        retryTemplate.setBackOffPolicy(fixedBackOffPolicy);

        MaxAttemptsRetryPolicy retryPolicy = new MaxAttemptsRetryPolicy();
        retryPolicy.setMaxAttempts(5);
        retryTemplate.setRetryPolicy(retryPolicy);

    }

    /**
     * Сохраняет информацию о запросе к эндпоинту (POST /hit)
     *
     * @param hit Входящий EndpointHitDto
     */
    public void saveHit(EndpointHitDto hit) {
        RestClient restClient = RestClient.builder().baseUrl(makeUri()).build();
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
        RestClient restClient = RestClient.builder().baseUrl(makeUri()).build();
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

    private String makeUri() {
        ServiceInstance instance = retryTemplate.execute(cxt -> getInstance());
        return ("http://" + instance.getHost() + ":" + instance.getPort());
    }

    private ServiceInstance getInstance() {
        try {
            return discoveryClient
                    .getInstances(statsServiceId)
                    .getFirst();
        } catch (Exception exception) {
            throw new StatsServerUnavailable(
                    "Ошибка обнаружения адреса сервиса статистики с id: " + statsServiceId);
        }
    }
}