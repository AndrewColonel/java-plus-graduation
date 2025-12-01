package ru.practicum.compilations.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.compilations.dto.CompilationDto;
import ru.practicum.compilations.dto.PublicCompilationRequestParamsDto;

import java.util.List;

public interface PublicCompilationControllerOperations {
    @GetMapping("/{complId}")
    CompilationDto getCompilationById(@PathVariable Long complId);

    @GetMapping
    List<CompilationDto> getCompilationsList(PublicCompilationRequestParamsDto params);
}
