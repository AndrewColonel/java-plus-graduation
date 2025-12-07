package ru.practicum.event.client.category;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.event.dto.ext.CategoryDto;


import java.util.List;
import java.util.Set;


@FeignClient(name = "category-service", fallback = CategoryClientFallback.class)
public interface CategoryClient {

    @GetMapping("/categories/{catId}")
    CategoryDto getById(@Positive @NotNull @PathVariable(value = "catId") Long catId);

    @GetMapping("/admin/categories")
    Set<CategoryDto> findByIdIn(@RequestParam(name = "ids", required = false) List<Long> ids);

}
