package ru.practicum.category.client;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.category.dto.CategoryDto;

@FeignClient(name = "category-service", path = "/categories", fallback = CategoryPublicClientFallback.class)
public interface CategoryPublicClient {

    @GetMapping("/{catId}")
    CategoryDto getById(@Positive @NotNull @PathVariable(value = "catId") Long catId);
}
