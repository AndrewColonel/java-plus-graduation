package ru.practicum.comment.service;

import ru.practicum.comment.dto.*;
import ru.practicum.comment.model.CommentStatus;

import java.util.List;

public interface CommentsService {
    // Public
    List<CommentDto> getCommentPublic(GetCommentsParam param);

    // Private
    CommentDto createCommentPrivate(NewCommentDto newCommentDto, CreateCommentParam createCommentParam);

    CommentDto updateCommentPrivate(NewCommentDto newCommentDto, UpdateCommentParam updateCommentParam);

    CommentDto deleteCommentPrivate(UpdateCommentParam updateCommentParam);

    List<CommentDto> getCommentPrivate(GetCommentsParam param, Long userId);

    // Admin
    List<CommentDto> searchCommentAdmin(SearchCommentParam param, CommentStatus status);

    CommentDto updateCommentAdmin(Long commentId, CommentStatus commentStatus);
}
