package ru.practicum.category.controller;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.category.dto.CategoryDto;

import java.util.Collection;

public interface CategoryControllerPublicOperations {
    @GetMapping
    Collection<CategoryDto> getAll(
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0", required = false) Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "10", required = false) Integer size);

    @GetMapping("/{catId}")
    CategoryDto getById(@Positive @NotNull @PathVariable(value = "catId") Long catId);
}
