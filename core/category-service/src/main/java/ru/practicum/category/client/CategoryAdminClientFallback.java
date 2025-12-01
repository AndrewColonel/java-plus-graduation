package ru.practicum.category.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.dto.UpdateCategoryDto;

@Slf4j
@Component
public class CategoryAdminClientFallback implements CategoryAdminClient {
    @Override
    public CategoryDto create(NewCategoryDto newCategoryDto) {
        log.warn("Fallback CategoryAdminClient response: сервис create временно недоступен");
        return null;
    }

    @Override
    public CategoryDto update(Long catId, UpdateCategoryDto updateCategoryDto) {
        log.warn("Fallback CategoryAdminClient response: сервис update временно недоступен");
        return null;
    }

    @Override
    public void delete(Long catId) {
        log.warn("Fallback CategoryAdminClient response: сервис delete временно недоступен");
    }
}
