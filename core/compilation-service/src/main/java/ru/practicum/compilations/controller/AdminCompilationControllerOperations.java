package ru.practicum.compilations.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilations.dto.AdminNewCompilationParamDto;
import ru.practicum.compilations.dto.AdminUpdateCompilationParamDto;
import ru.practicum.compilations.dto.CompilationDto;

public interface AdminCompilationControllerOperations {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    CompilationDto addCompilation(
            @Valid @RequestBody AdminNewCompilationParamDto adminNewCompilationParamDto
    );

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteCompilation(
            @PathVariable Long compId
    );

    @PatchMapping("/{compId}")
    CompilationDto updateCompilation(
            @PathVariable Long compId,
            @Valid @RequestBody AdminUpdateCompilationParamDto adminUpdateCompilationParamDto
    );
}
