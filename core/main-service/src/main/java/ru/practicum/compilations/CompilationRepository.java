package ru.practicum.compilations;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompilationRepository extends JpaRepository<Compilation, Long> {

    /**
     * Находит все подборки с указанным статусом закрепления
     *
     * @param pinned   статус закрепления
     * @param pageable параметры пагинации
     * @return List<Compilation> с подборками
     */
    List<Compilation> findAllByPinned(Boolean pinned, Pageable pageable);

}
