package ru.practicum.event.client.category;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.event.dto.ext.CategoryDto;

import java.util.List;


@Slf4j
@Component
public class CategoryClientFallback implements CategoryClient {

    @Override
    public CategoryDto getById(Long catId) {
        log.warn("Fallback CategoryClient response: сервис getById временно недоступен");
        return null;
    }

    @Override
    public List<CategoryDto> findByIdIn(List<Long> ids) {
        log.warn("Fallback CategoryClient response: сервис findByIdIn временно недоступен");
        return List.of();
    }
}
