package ru.practicum.event.client.category;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.event.dto.ext.CategoryDto;

import java.util.List;
import java.util.Set;


@Slf4j
@Component
public class CategoryClientFallback implements CategoryClient {

    @Override
    public CategoryDto getById(Long catId) {
        log.warn("!==========================================================================|");
        log.warn("| Fallback CategoryClient response: сервис getById временно недоступен |");
        log.warn("!==========================================================================|");
        return null;
    }

    @Override
    public Set<CategoryDto> findByIdIn(List<Long> ids) {
        log.warn("!==========================================================================|");
        log.warn("Fallback CategoryClient response: сервис findByIdIn временно недоступен");
        log.warn("!==========================================================================|");
        return Set.of();
    }
}
