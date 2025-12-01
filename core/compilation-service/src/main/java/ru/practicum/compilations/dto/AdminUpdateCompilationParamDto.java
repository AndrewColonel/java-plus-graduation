package ru.practicum.compilations.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminUpdateCompilationParamDto {
    private List<Long> events;

    private Boolean pinned;

    @Size(min = 1, max = 50, message = "Длинна названия подборки должна быть от 1 до 50 символов")
    private String title;
}