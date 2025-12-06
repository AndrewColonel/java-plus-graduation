package ru.practicum.comment.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.GetCommentsParam;
import ru.practicum.comment.service.CommentsService;

import java.util.List;

@RestController
@RequestMapping("/events/{eventId}/comments")
@AllArgsConstructor
@Slf4j
public class PublicCommentsController {

    private final CommentsService commentsService;

    // GET /events/{eventId}/comments
    @GetMapping
    public List<CommentDto> getAll(@Valid @ModelAttribute GetCommentsParam param) {
        log.warn("PUBLIC Запрос на получение списка всех комментариев {}", param);
        return commentsService.getCommentPublic(param);
    }
}
