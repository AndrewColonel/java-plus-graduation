package ru.practicum.compilations.model;


import org.springframework.stereotype.Component;
import ru.practicum.compilations.dto.AdminNewCompilationParamDto;
import ru.practicum.compilations.dto.CompilationDto;
import ru.practicum.compilations.dto.EventShortDto;
import ru.practicum.compilations.model.entity.Compilation;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


@Component
public class CompilationMapper {
    public static Compilation toEntity(AdminNewCompilationParamDto dto) {
        return Compilation.builder()
                .title(dto.getTitle())
                .pinned(dto.getPinned())
                .events(new HashSet<>(dto.getEvents()))
                .build();
    }

    public static CompilationDto toDto(Compilation entity, Set<EventShortDto> events ) {
        return CompilationDto.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .pinned(entity.getPinned())
                .events(new ArrayList<>(events))
                .build();
    }

    public static CompilationDto toDto(Compilation entity, Map<Long,EventShortDto> eventShortDtoMap) {
        return CompilationDto.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .pinned(entity.getPinned())
                .events(entity.getEvents().stream()
                        .map(eventShortDtoMap::get)
                        .toList())
                .build();
    }

//    public static Compilation toEntity(AdminNewCompilationParamDto dto, Set<Event> events) {
//        return Compilation.builder()
//                .title(dto.getTitle())
//                .pinned(dto.getPinned())
//                .events(events)
//                .build();
//    }


}
