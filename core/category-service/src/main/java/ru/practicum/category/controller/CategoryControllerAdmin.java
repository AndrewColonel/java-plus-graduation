package ru.practicum.category.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.service.CategoryService;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.dto.UpdateCategoryDto;


@RestController
@Slf4j
@Validated
@RequestMapping("/admin/categories")
@AllArgsConstructor
public class CategoryControllerAdmin implements CategoryControllerAdminOperations {

    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    @Override
    public CategoryDto create(@Valid @RequestBody NewCategoryDto newCategoryDto) {
        log.warn(">>> CategoryControllerAdmin: POST /admin/categories");
        log.warn(">>> Запрос на создание категории {}", newCategoryDto);
        CategoryDto dto = categoryService.createCategory(newCategoryDto);
        log.warn("ИТОГ: создание категории : {}", dto);
        return dto;


    }

    @PatchMapping("/{catId}")
    @Override
    public CategoryDto update(@Positive @NotNull @PathVariable("catId") Long catId,
                              @Valid @RequestBody UpdateCategoryDto updateCategoryDto) {
        log.warn(">>> CategoryControllerAdmin: PATCH /admin/categories/{}", catId);
        log.warn(">>> Запрос на изменение {} категории с ID = {}", updateCategoryDto, catId);
        CategoryDto dto = categoryService.updateCategory(catId, updateCategoryDto);
        log.warn("ИТОГ: изменение категории {}", dto);
        return dto;
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @Override
    public void delete(@Positive @NotNull @PathVariable("catId") Long catId) {
        log.warn(">>> CategoryControllerAdmin: DELETE /admin/categories/{}", catId);
        log.warn(">>> Запрос на удление категории с ID = {}", catId);
        categoryService.deleteCategory(catId);
        log.warn("ИТОГ: категория с ID = {} удалена", catId);
    }
}
