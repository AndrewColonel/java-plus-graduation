package ru.practicum.comment.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.SearchCommentParam;
import ru.practicum.comment.model.CommentStatus;
import ru.practicum.comment.service.CommentsService;

import java.util.List;

@RestController
@RequestMapping("/admin")
@AllArgsConstructor
@Slf4j
@Validated
public class AdminCommentsController {

    private final CommentsService commentsService;

    // GET /admin/events/{eventId}/comments
    @GetMapping("/events/{eventId}/comments")
    public List<CommentDto> getAll(@RequestParam(defaultValue = "OPEN") CommentStatus status,
                                   @Valid @ModelAttribute SearchCommentParam param) {
        log.warn("ADMIN Запрос на поиск комментариев для собятия {}", param.getEventId());
        return commentsService.searchCommentAdmin(param, status);

    }

    // PATCH /admin/comments/{commentId}
    @PatchMapping("/comments/{commentId}")
    public CommentDto moderate(@Positive @PathVariable Long commentId,
                               @RequestParam(defaultValue = "MODERATED_HIDDEN") CommentStatus status) {
        log.warn("ADMIN запрос на модерацию комментария {}", commentId);
        return commentsService.updateCommentAdmin(commentId, status);
    }
}
