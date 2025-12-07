package ru.practicum.category.controller;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.service.CategoryService;
import ru.practicum.category.dto.CategoryDto;

import java.util.Collection;

@RestController
@AllArgsConstructor
@Slf4j
@Validated
@RequestMapping("/categories")
public class CategoryControllerPublic {

    private final CategoryService categoryService;

    @GetMapping
    public Collection<CategoryDto> getAll(
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0", required = false) Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "10", required = false) Integer size) {
        log.warn(">>> CategoryControllerPublic: GET /categories");
        log.warn(">>> Запрос на выдачу списка из {} категорий, начиная с ID = {}", size, from);
        Collection<CategoryDto> dtoCollection = categoryService.getAllCategory(from, size);
        log.warn("ИТОГ: список с данными категорий {}", dtoCollection);
        return dtoCollection;
    }

    @GetMapping("/{catId}")
      public CategoryDto getById(@Positive @NotNull @PathVariable(value = "catId") Long catId) {
        log.warn(">>> CategoryControllerPublic: GET /categories/{}", catId);
        log.warn(">>> Запрос категории с ID = {}", catId);
        CategoryDto dto = categoryService.getCategoryById(catId);
        log.warn("ИТОГ: получены данные по категории {}", dto);
        return dto;
    }
}
