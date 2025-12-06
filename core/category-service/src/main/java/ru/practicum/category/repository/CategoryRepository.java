package ru.practicum.category.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.category.model.entity.Category;

import java.util.List;
import java.util.Set;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Set<Category> findByIdIn(List<Long> id);

}
