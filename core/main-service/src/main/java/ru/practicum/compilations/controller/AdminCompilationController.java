package ru.practicum.compilations.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilations.dto.AdminNewCompilationParamDto;
import ru.practicum.compilations.dto.AdminUpdateCompilationParamDto;
import ru.practicum.compilations.dto.CompilationDto;
import ru.practicum.compilations.service.CompilationService;


@RestController
@RequestMapping("/admin/compilations")
@RequiredArgsConstructor
@Slf4j
public class AdminCompilationController {

    private final CompilationService compilationService;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto addCompilation(
            @Valid @RequestBody AdminNewCompilationParamDto adminNewCompilationParamDto
    ) {
        log.warn(">>> AdminCompilationController: POST /admin/compilations");
        log.warn(">>> Добавление новой подборки с параметрами {} ", adminNewCompilationParamDto);
        CompilationDto dto = compilationService.addCompilation(adminNewCompilationParamDto);
        log.warn("ИТОГ: подборка с параметрами добавлена {}", dto);
        return dto;
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(
            @PathVariable Long compId
    ) {
        log.warn(">>> AdminCompilationController: DELETE /admin/compilations/{}", compId);
        log.warn(">>> Удаление подборки с ID = {} ", compId);
        compilationService.deleteCompilation(compId);
        log.warn("ИТОГ: подборк с ID = {} удалена ", compId);
    }


    @PatchMapping("/{compId}")
    public CompilationDto updateCompilation(
            @PathVariable Long compId,
            @Valid @RequestBody AdminUpdateCompilationParamDto adminUpdateCompilationParamDto
    ) {
        log.warn(">>> AdminCompilationController: PATCH /admin/compilations/{}", compId);
        log.warn(">>> Обновление информации подборки с ID = {} на информацию с параметрами {} ",
                compId, adminUpdateCompilationParamDto);
        CompilationDto dto = compilationService.updateCompilation(compId, adminUpdateCompilationParamDto);
        log.warn("ИТОГ: информации подборки с ID = {} изменена на {}", compId, adminUpdateCompilationParamDto);
        return dto;
    }
}
