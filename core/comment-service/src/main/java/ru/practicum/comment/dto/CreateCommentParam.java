package ru.practicum.comment.dto;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCommentParam {
    @Positive
    private Long userId;
    @Positive
    private Long eventId;

}
