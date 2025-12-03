package ru.practicum.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.comment.dto.ext.UserShortDto;
import ru.practicum.comment.model.CommentStatus;



import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    private Long id;
    private Long eventId;
    private String text;
    private UserShortDto creator;
    private LocalDateTime createTime;
    private CommentStatus status;
}
