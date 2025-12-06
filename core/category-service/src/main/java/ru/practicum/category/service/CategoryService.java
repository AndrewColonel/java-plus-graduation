package ru.practicum.category.service;

import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.dto.UpdateCategoryDto;
import ru.practicum.category.model.entity.Category;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface CategoryService {
    Collection<CategoryDto> getAllCategory(Integer from, Integer size);

    CategoryDto getCategoryById(Long catId);

    CategoryDto createCategory(NewCategoryDto newCategoryDto);

    CategoryDto updateCategory(Long catId, UpdateCategoryDto updateCategoryDto);

    void deleteCategory(Long catId);

    Category getCategory(Long catId);

    Set<CategoryDto> findCategoryByIdIn(List<Long> ids);

}
