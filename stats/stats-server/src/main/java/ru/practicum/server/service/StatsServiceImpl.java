package ru.practicum.server.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.compilations.dto.EndpointHitDto;
import ru.practicum.compilations.dto.ViewStatsDto;
import ru.practicum.server.common.GetStatsRequest;
import ru.practicum.server.mapper.StatsMapper;
import ru.practicum.server.repository.EndpointHitRepository;

import java.util.Collection;

import static ru.practicum.server.mapper.StatsMapper.toEndpointHit;

@Service
@Slf4j
@AllArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final EndpointHitRepository endpointHitRepository;

    @Override
    public void createEndpointHit(EndpointHitDto endpointHitDto) {
        endpointHitRepository.save(toEndpointHit(endpointHitDto));
    }

    @Override
    public Collection<ViewStatsDto> findViewStat(GetStatsRequest getStatsRequest) {
        // если списко uris  не задан
        if (getStatsRequest.getUris().isEmpty()) {
            if (getStatsRequest.getUnique())
                return endpointHitRepository.countIniqueHitsByIp(
                                getStatsRequest.getStart(),
                                getStatsRequest.getEnd())
                        .stream()
                        .map(StatsMapper::toViewStatsDto)
                        .toList();

            return endpointHitRepository.countHitsByIp(
                            getStatsRequest.getStart(),
                            getStatsRequest.getEnd())
                    .stream()
                    .map(StatsMapper::toViewStatsDto)
                    .toList();
        }

        // если списко uris задан
        if (getStatsRequest.getUnique())
            return endpointHitRepository.countIniqueHitsByIp(
                            getStatsRequest.getStart(),
                            getStatsRequest.getEnd(),
                            getStatsRequest.getUris())
                    .stream()
                    .map(StatsMapper::toViewStatsDto)
                    .toList();

        return endpointHitRepository.countHitsByIp(
                        getStatsRequest.getStart(),
                        getStatsRequest.getEnd(),
                        getStatsRequest.getUris())
                .stream()
                .map(StatsMapper::toViewStatsDto)
                .toList();


    }


}
