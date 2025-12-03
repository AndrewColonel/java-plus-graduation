package ru.practicum.category.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.category.dto.CategoryDto;


@Slf4j
@Component
public class CategoryPublicClientFallback implements CategoryPublicClient {

    @Override
    public CategoryDto getById(Long catId) {
        log.warn("Fallback CategoryPublicClient response: сервис getById временно недоступен");
        return null;
    }
}
