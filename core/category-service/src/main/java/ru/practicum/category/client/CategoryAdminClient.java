package ru.practicum.category.client;

import org.springframework.cloud.openfeign.FeignClient;
import ru.practicum.category.controller.CategoryControllerAdminOperations;

@FeignClient(name = "category-service", path = "/admin/categories", fallback = CategoryAdminClientFallback.class)
public interface CategoryAdminClient extends CategoryControllerAdminOperations {
}
