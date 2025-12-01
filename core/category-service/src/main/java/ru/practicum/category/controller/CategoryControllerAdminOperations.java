package ru.practicum.category.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.dto.UpdateCategoryDto;

public interface CategoryControllerAdminOperations {
    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    CategoryDto create(@Valid @RequestBody NewCategoryDto newCategoryDto);

    @PatchMapping("/{catId}")
    CategoryDto update(@Positive @NotNull @PathVariable("catId") Long catId,
                       @Valid @RequestBody UpdateCategoryDto updateCategoryDto);

    @DeleteMapping("/{catId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    void delete(@Positive @NotNull @PathVariable("catId") Long catId);
}
