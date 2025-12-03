package ru.practicum.comment.model;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.model.entity.Comment;



@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentMapper {

    public static CommentDto toDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .eventId(comment.getEvent())
                .text(comment.getText())
                .creator(UserMapper.toUserShortDto(comment.getCreator()))
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
