package ru.practicum.comment.service;

import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.practicum.comment.repository.CommentSpecification;
import ru.practicum.comment.repository.CommentsRepository;
import ru.practicum.comment.dto.*;
import ru.practicum.comment.model.Comment;
import ru.practicum.comment.model.CommentStatus;
import ru.practicum.comment.model.CommentMapper;
import ru.practicum.event.EventRepository;
import ru.practicum.event.model.Event;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.user.User;
import ru.practicum.user.UserRepository;

import java.util.List;

import static ru.practicum.comment.model.CommentMapper.toComment;
import static ru.practicum.comment.model.CommentMapper.toDto;


@Service
@AllArgsConstructor
public class CommentsServiceImpl implements CommentsService {

    private final CommentsRepository commentsRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    // Public
    @Override
    public List<CommentDto> getCommentPublic(GetCommentsParam param) {
        Event event = getEvent(param.getEventId());
        // для публичного показа допускаются комментарии после модерации администртором
        return commentsRepository.findByEventIdAndStatusOrderByCreateTimeDesc(event.getId(),
                        CommentStatus.MODERATED_OPEN, param.toPage())
                .stream()
                .map(CommentMapper::toDto)
                .toList();
    }

    // Private
    @Override
    public CommentDto createCommentPrivate(NewCommentDto newCommentDto, CreateCommentParam param) {
        Event event = getEvent(param.getEventId());
        User creator = getUser(param.getUserId());
        Comment comment = toComment(newCommentDto);
        comment.setCreator(creator);
        comment.setEvent(event);
        comment.setStatus(CommentStatus.OPEN);
        return toDto(commentsRepository.save(comment));
    }

    @Override
    public CommentDto updateCommentPrivate(NewCommentDto newCommentDto, UpdateCommentParam param) {
        Comment checkedComment = checkCommentAvailability(param);
        checkedComment.setText(newCommentDto.getText());
        if (param.getStatus() != null) {
            checkedComment.setStatus(checkCreatorCommentStatus(param.getStatus()));
        }
        return toDto(commentsRepository.save(checkedComment));
    }

    @Override
    public List<CommentDto> getCommentPrivate(GetCommentsParam param, Long userId) {
        return commentsRepository.findByEventIdAndCreatorIdOrderByCreateTimeDesc(param.getEventId(),
                        userId, param.toPage()).stream()
                .map(CommentMapper::toDto)
                .toList();
    }

    @Override
    public CommentDto deleteCommentPrivate(UpdateCommentParam param) {
        Comment checkedComment = checkCommentAvailability(param);
        commentsRepository.delete(checkedComment);
        return toDto(checkedComment);
    }

    // Admin
    @Override
    public List<CommentDto> searchCommentAdmin(SearchCommentParam param, CommentStatus status) {
        Specification<Comment> spec = CommentSpecification.byParams(param, status);
        return commentsRepository.findAll(spec, param.toPage()).getContent().stream()
                .map(CommentMapper::toDto)
                .toList();
    }

    @Override
    public CommentDto updateCommentAdmin(Long commentId, CommentStatus commentStatus) {
        Comment comment = getComment(commentId);
        // можно модерировать только комментарии со статусом OPEN
        if (comment.getStatus().equals(CommentStatus.HIDDEN))
            throw new ValidationException(String.format("Комментарий еще не опубликован, status = %s",
                    comment.getStatus()));
        comment.setStatus(checkAdminCommentStatus(commentStatus));
        return toDto(commentsRepository.save(comment));
    }

    // вспомогательные методы
    private Event getEvent(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException(String.format("Событие с ID %s не найдено",
                        eventId)));
    }

    private User getUser(Long commentatorId) {
        return userRepository.findById(commentatorId).orElseThrow(
                () -> new NotFoundException(String.format("Комментатор с ID %s не найден",
                        commentatorId)));
    }

    private Comment getComment(Long commentId) {
        return commentsRepository.findById(commentId).orElseThrow(
                () -> new NotFoundException(String.format("Комментарий с ID %s не найден",
                        commentId)));
    }

    // метод проверяет доступность комментария для изменения
    private Comment checkCommentAvailability(UpdateCommentParam param) {
        Event event = getEvent(param.getEventId());
        Comment comment = getComment(param.getCommentId());
        if (!comment.getEvent().getId().equals(event.getId())) {
            throw new ValidationException(String.format("Комментарий с id %s не относится к событию с id %s",
                    comment.getId(), event.getId()));
        }
        // редакция комментария возможно только до модерациии администратором
        if (comment.getStatus().equals(CommentStatus.MODERATED_HIDDEN)
                || comment.getStatus().equals(CommentStatus.MODERATED_OPEN)) {
            throw new ValidationException(
                    String.format("Изменить данный коммментарий после модерации нельзя status = %s",
                            comment.getStatus()));
        }
        User commentator = getUser(param.getUserId());
        // Можно менять только свой комментарий
        if (!comment.getCreator().getId().equals(commentator.getId())) {
            throw new ValidationException(
                    String.format("Можно менять только свои комментарии, " +
                                    "id польтзователя %s не равен id комментатора %s",
                            comment.getCreator().getId(), commentator.getId()));
        }
        return comment;
    }

    // комментатор не может выставлять статус MODERATED_OPEN\HIDDEN при создании комментария
    private CommentStatus checkCreatorCommentStatus(CommentStatus status) {
        if (status.equals(CommentStatus.MODERATED_HIDDEN) || status.equals(CommentStatus.MODERATED_OPEN))
            throw new ValidationException(String.format("Пользователь не может ставить статус %s", status));
        return status;
    }

    // администратор не может выставлять статус OPEN\HIDDEN при модерации комментария
    private CommentStatus checkAdminCommentStatus(CommentStatus status) {
        if (status.equals(CommentStatus.HIDDEN) || status.equals(CommentStatus.OPEN))
            throw new ValidationException(String.format("Администртор не может ставить статус %s", status));
        return status;
    }


}
