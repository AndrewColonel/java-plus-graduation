package ru.practicum.requests.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestDto {
    @NotNull(message = "Creation date cannot be null")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created;

    @NotNull(message = "Код события не может быть null")
    private Long event;

    @NotNull(message = "Код запроса не может быть null")
    private Long id;

    @NotNull(message = "Код пользователя не может быть null")
    private Long requester;

    @NotNull(message = "Статус не может быть null")
    private String status;
}