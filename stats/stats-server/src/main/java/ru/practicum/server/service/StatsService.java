package ru.practicum.server.service;

import ru.practicum.compilations.dto.EndpointHitDto;
import ru.practicum.compilations.dto.ViewStatsDto;
import ru.practicum.server.common.GetStatsRequest;

import java.util.Collection;

public interface StatsService {

    void createEndpointHit(EndpointHitDto endpointHitDto);

    Collection<ViewStatsDto> findViewStat(GetStatsRequest getStatsRequest);
}
