package ru.practicum.server.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilations.dto.EndpointHitDto;
import ru.practicum.compilations.dto.ViewStatsDto;
import ru.practicum.server.common.GetStatsRequest;
import ru.practicum.server.service.StatsService;

import java.util.Collection;
import java.util.List;

@RestController
@AllArgsConstructor
@Slf4j
@Validated
public class StatsController {

    private final StatsService statsService;

    @PostMapping("/hit")
    // в соответсвии с ТЗ - данный метод VOID и выдает код 201
    @ResponseStatus(value = HttpStatus.CREATED)
    public void create(@RequestBody @Valid EndpointHitDto endpointHitDto) {
        log.error("Создание записи {}", endpointHitDto);
        statsService.createEndpointHit(endpointHitDto);
    }

    @GetMapping("/stats")
    public Collection<ViewStatsDto> findAll(
            @RequestParam(name = "start") String start,
            @RequestParam(name = "end") String end,
            @RequestParam(name = "uris", required = false) List<String> uris,
            @RequestParam(name = "unique", required = false, defaultValue = "false") String unique
    ) {
        log.error("Запрос записей статистики с {} по {}", start, end);
        return statsService.findViewStat(
                GetStatsRequest.of(start, end, uris, unique));
    }
}
