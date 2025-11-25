package ru.practicum.category;

import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.dto.UpdateCategoryDto;

import java.util.Collection;

public interface CategoryService {
    Collection<CategoryDto> getAllCategory(Integer from, Integer size);

    CategoryDto getCategoryById(Long catId);

    CategoryDto createCategory(NewCategoryDto newCategoryDto);

    CategoryDto updateCategory(Long catId, UpdateCategoryDto updateCategoryDto);

    void deleteCategory(Long catId);

    Category getCategory(Long catId);
}
