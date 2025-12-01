package ru.practicum.category.client;

import org.springframework.cloud.openfeign.FeignClient;
import ru.practicum.category.controller.CategoryControllerPublicOperations;

@FeignClient(name = "category-service", path = "/categories", fallback = CategoryPublicClientFallback.class)
public interface CategoryPublicClient extends CategoryControllerPublicOperations {
}
