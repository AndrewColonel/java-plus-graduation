package ru.practicum.compilations;


import org.springframework.stereotype.Component;
import ru.practicum.compilations.dto.AdminNewCompilationParamDto;
import ru.practicum.compilations.dto.CompilationDto;
import ru.practicum.event.EventMapper;
import ru.practicum.event.model.Event;

import java.util.Set;
import java.util.stream.Collectors;


@Component
public class CompilationMapper {
    public static Compilation toEntity(AdminNewCompilationParamDto dto) {
        return Compilation.builder()
                .title(dto.getTitle())
                .pinned(dto.getPinned())
                .build();
    }

    public static CompilationDto toDto(Compilation entity) {
        return CompilationDto.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .pinned(entity.getPinned())
                .events(entity.getEvents().stream()
                        .map(EventMapper::toEventShortDto)
                        .collect(Collectors.toList()))
                .build();
    }

    public static Compilation toEntity(AdminNewCompilationParamDto dto, Set<Event> events) {
        return Compilation.builder()
                .title(dto.getTitle())
                .pinned(dto.getPinned())
                .events(events)
                .build();
    }


}
