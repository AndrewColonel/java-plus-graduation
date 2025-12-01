package ru.practicum.compilations.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.compilations.dto.CompilationDto;
import ru.practicum.compilations.dto.PublicCompilationRequestParamsDto;
import ru.practicum.compilations.service.CompilationService;

import java.util.List;

@RestController
@RequestMapping("/compilations")
@Slf4j
@RequiredArgsConstructor
public class PublicCompilationController implements PublicCompilationControllerOperations {

    private final CompilationService compilationService;

    @GetMapping("/{complId}")
    @Override
    public CompilationDto getCompilationById(@PathVariable Long complId) {
        log.warn(">>> PublicCompilationController: GET /compilations/{complId}");
        log.warn(">>> Получение подборки по ID = {} ", complId);

        CompilationDto dto = compilationService.getCompilationById(complId);
        log.warn("ИТОГ: Данные подборки {}", dto);
        return dto;
    }

    @GetMapping
    @Override
    public List<CompilationDto> getCompilationsList(PublicCompilationRequestParamsDto params) {
        log.warn(">>> PublicCompilationController: GET /compilations");
        log.warn(">>> Получение подборок событий. Параметры запроса: " +
                        "Фильтр по закреплению: {}, " +
                        "пропускаем {} элементов, " +
                        "смотрим {} шт",
                params.getPinned() != null ? params.getPinned() : "не указан",
                params.getFrom(),
                params.getSize());

        List<CompilationDto> list = compilationService.getCompilationsList(params);
        log.warn("ИТОГ: по заданым параметрам найдена  подборка событий {}", list);
        return list;
    }

}
