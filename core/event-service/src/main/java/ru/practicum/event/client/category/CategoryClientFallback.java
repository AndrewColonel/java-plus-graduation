package ru.practicum.event.client.category;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.category.dto.CategoryDto;


@Slf4j
@Component
public class CategoryClientFallback implements CategoryClient {

    @Override
    public CategoryDto getById(Long catId) {
        log.warn("Fallback CategoryClient response: сервис getById временно недоступен");
        return null;
    }
}
