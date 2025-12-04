package ru.practicum.category.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.dto.UpdateCategoryDto;
import ru.practicum.category.model.entity.Category;
import ru.practicum.category.model.CategoryMapper;
import ru.practicum.exception.NotFoundException;

import java.util.Collection;
import java.util.List;

import static ru.practicum.category.model.CategoryMapper.toCategory;
import static ru.practicum.category.model.CategoryMapper.toCategoryDto;

@Service
@AllArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public Collection<CategoryDto> getAllCategory(Integer from, Integer size) {
        return categoryRepository.findAll(PageRequest.of(from / size, size)).stream()
                .map(CategoryMapper::toCategoryDto)
                .toList();
    }

    @Override
    public CategoryDto getCategoryById(Long catId) {
        return toCategoryDto(getCategory(catId));
    }

    @Override
    public CategoryDto createCategory(NewCategoryDto newCategoryDto) {
        // для обработки в ErrorHandler - DuplicatedDataException.class, DataIntegrityViolationException.class
        // HttpStatus.CONFLICT)
        return toCategoryDto(categoryRepository.save(toCategory(newCategoryDto)));
    }

    @Override
    public CategoryDto updateCategory(Long catId, UpdateCategoryDto updateCategoryDto) {
        Category category = getCategory(catId);
        category.setName(updateCategoryDto.getName());
        return toCategoryDto(categoryRepository.save(category));
    }

    @Override
    public void deleteCategory(Long catId) {
        Category category = getCategory(catId);
        categoryRepository.delete(category);
    }

    @Override
    public List<CategoryDto> findCategoryByIdIn(List<Long> ids) {
        return categoryRepository.findByIdIn(ids).stream()
                .map(CategoryMapper::toCategoryDto)
                .toList();
    }

    // вспомогательный метод
    public Category getCategory(Long catId) {
        return categoryRepository.findById(catId).orElseThrow(
                // для обработки в ErrorHandler - NotFoundException - HttpStatus.NOT_FOUND
                () -> new NotFoundException(String.format("Категории с ID = %s не существует", catId)));
    }
}
