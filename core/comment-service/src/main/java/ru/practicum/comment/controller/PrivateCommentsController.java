package ru.practicum.comment.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.dto.*;
import ru.practicum.comment.service.CommentsService;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events/{eventId}/comments")
@AllArgsConstructor
@Validated
@Slf4j
public class PrivateCommentsController {

    private final CommentsService commentsService;

    // POST /users/{userId}/events/{eventId}/comments
    @PostMapping
    public CommentDto create(@Valid @RequestBody NewCommentDto newCommentDto,
                             @Valid @ModelAttribute CreateCommentParam param) {
        log.warn("PRIVATE Запрос на создание нового комментария пользователем {} к событию {}",
                param.getUserId(), param.getEventId());
        return commentsService.createCommentPrivate(newCommentDto, param);
    }

    // PATCH /users/{userId}/events/{eventId}/comments/{commentId}
    @PatchMapping("/{commentId}")
    public CommentDto update(@Valid @RequestBody(required = false) NewCommentDto newCommentDto,
                             @Valid @ModelAttribute UpdateCommentParam param) {
        log.warn("PRIVATE Запрос на обновление комментария {} пользователем {} к событию {}",
                param.getCommentId(), param.getUserId(), param.getEventId());
        return commentsService.updateCommentPrivate(newCommentDto, param);
    }

    // DELETE /users/{userId}/events/{eventId}/comments/{commentId}
    @DeleteMapping("/{commentId}")
    public CommentDto delete(@Valid @ModelAttribute UpdateCommentParam param) {
        log.warn("PRIVATE Запрос на удаление комментария {} пользователем {} к событию {}",
                param.getCommentId(), param.getUserId(), param.getEventId());
        return commentsService.deleteCommentPrivate(param);
    }

    // GET /users/{userId}/events/{eventId}/comments
    @GetMapping
    public List<CommentDto> getAll(@Positive @PathVariable Long userId,
                                   @Valid @ModelAttribute GetCommentsParam param) {
        log.warn("PRIVATE Запрос на получение списка всех комментариев события  {} для пользователя {}",
                param.getEventId(), userId);
        return commentsService.getCommentPrivate(param, userId);
    }
}
