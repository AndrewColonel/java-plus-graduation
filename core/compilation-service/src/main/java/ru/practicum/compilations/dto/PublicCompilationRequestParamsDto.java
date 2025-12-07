package ru.practicum.compilations.dto;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PublicCompilationRequestParamsDto {
    private Boolean pinned;

    @Min(value = 0, message = "Параметр 'from' должен быть не меньше 0")
    @Builder.Default
    private int from = 0;

    @Min(value = 1, message = "Параметр 'size' должен быть не меньше 1")
    @Builder.Default
    private int size = 10;

    public Pageable toPageable() {
        return PageRequest.of(from / size, size);
    }
}