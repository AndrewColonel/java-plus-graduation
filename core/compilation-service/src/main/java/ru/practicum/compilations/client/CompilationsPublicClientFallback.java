package ru.practicum.compilations.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.compilations.dto.CompilationDto;
import ru.practicum.compilations.dto.PublicCompilationRequestParamsDto;

import java.util.List;

@Slf4j
@Component
public class CompilationsPublicClientFallback implements CompilationsPublicClient{
    @Override
    public CompilationDto getCompilationById(Long complId) {
        log.warn("Fallback CompilationsPublicClient response: сервис getCompilationById временно недоступен");
        return null;
    }

    @Override
    public List<CompilationDto> getCompilationsList(PublicCompilationRequestParamsDto params) {
        log.warn("Fallback CompilationsPublicClient response: сервис getCompilationsList временно недоступен");
        return List.of();
    }
}
