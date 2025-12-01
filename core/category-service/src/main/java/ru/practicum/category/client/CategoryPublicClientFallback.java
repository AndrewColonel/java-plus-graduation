package ru.practicum.category.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.category.dto.CategoryDto;

import java.util.Collection;
import java.util.List;

@Slf4j
@Component
public class CategoryPublicClientFallback implements CategoryPublicClient {
    @Override
    public Collection<CategoryDto> getAll(Integer from, Integer size) {
        log.warn("Fallback CategoryPublicClient response: сервис getAll временно недоступен");
        return List.of();
    }

    @Override
    public CategoryDto getById(Long catId) {
        log.warn("Fallback CategoryPublicClient response: сервис getById временно недоступен");
        return null;
    }
}
