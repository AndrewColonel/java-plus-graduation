package ru.practicum.comment.model;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.dto.ext.UserShortDto;
import ru.practicum.comment.model.entity.Comment;

import java.util.Map;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentMapper {

    public static CommentDto toDto(Comment comment, UserShortDto userShortDto) {
        return CommentDto.builder()
                .id(comment.getId())
                .eventId(comment.getEventId())
                .text(comment.getText())
                .creator(userShortDto)
                .createTime(comment.getCreateTime())
                .status(comment.getStatus())
                .build();
    }

    public static CommentDto toDto(Comment comment, Map<Long,UserShortDto> userShortDtoMap) {
        return CommentDto.builder()
                .id(comment.getId())
                .eventId(comment.getEventId())
                .text(comment.getText())
                .creator(userShortDtoMap.get(comment.getId()))
                .createTime(comment.getCreateTime())
                .status(comment.getStatus())
                .build();
    }

    public static Comment toComment(NewCommentDto newCommentDto) {
        return Comment.builder()
                .text(newCommentDto.getText())
                .build();
    }


}
