package ru.practicum.comment.dto;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.comment.model.CommentStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCommentParam {

    @Positive
    private Long userId;
    @Positive
    private Long eventId;
    @Positive
    private Long commentId;
    private CommentStatus status;

}
