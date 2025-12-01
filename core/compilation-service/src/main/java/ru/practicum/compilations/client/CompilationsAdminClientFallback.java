package ru.practicum.compilations.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.compilations.dto.AdminNewCompilationParamDto;
import ru.practicum.compilations.dto.AdminUpdateCompilationParamDto;
import ru.practicum.compilations.dto.CompilationDto;

@Slf4j
@Component
public class CompilationsAdminClientFallback implements CompilationsAdminClient {
    @Override
    public CompilationDto addCompilation(AdminNewCompilationParamDto adminNewCompilationParamDto) {
        log.warn("Fallback CompilationsAdminClient response: сервис addCompilation временно недоступен");
        return null;
    }

    @Override
    public void deleteCompilation(Long compId) {
        log.warn("Fallback CompilationsAdminClient response: сервис deleteCompilation временно недоступен");
    }

    @Override
    public CompilationDto updateCompilation(Long compId, AdminUpdateCompilationParamDto adminUpdateCompilationParamDto) {
        log.warn("Fallback CompilationsAdminClient response: сервис updateCompilation временно недоступен");
        return null;
    }
}
