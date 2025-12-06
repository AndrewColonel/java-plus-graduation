package ru.practicum.comment.service;

import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.practicum.comment.client.event.EventClient;
import ru.practicum.comment.client.user.UserClient;
import ru.practicum.comment.dto.ext.EventShortDto;
import ru.practicum.comment.dto.ext.UserShortDto;
import ru.practicum.comment.repository.CommentSpecification;
import ru.practicum.comment.repository.CommentsRepository;
import ru.practicum.comment.dto.*;
import ru.practicum.comment.model.entity.Comment;
import ru.practicum.comment.model.CommentStatus;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;


import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static ru.practicum.comment.model.CommentMapper.toComment;
import static ru.practicum.comment.model.CommentMapper.toDto;

@Service
@AllArgsConstructor
public class CommentsServiceImpl implements CommentsService {

    private final CommentsRepository commentsRepository;
    private final EventClient eventClient;
    private final UserClient userClient;

    // Public
    @Override
    public List<CommentDto> getCommentPublic(GetCommentsParam param) {
        EventShortDto event = getEvent(param.getEventId());
        List<Comment> commentList = commentsRepository.findByEventIdAndStatusOrderByCreateTimeDesc(event.getId(),
                CommentStatus.MODERATED_OPEN, param.toPage());
        // для публичного показа допускаются комментарии после модерации администртором
        Map<Long,UserShortDto> userShortDtoMap = getUserShortDtoMap(commentList);
        return commentList.stream()
                .map(c -> toDto(c, userShortDtoMap))
                .toList();
    }

    // Private
    @Override
    public CommentDto createCommentPrivate(NewCommentDto newCommentDto, CreateCommentParam param) {
        EventShortDto event = getEvent(param.getEventId());
        UserShortDto creator = getUser(param.getUserId());
        Comment comment = toComment(newCommentDto);
        comment.setCreatorId(creator.getId());
        comment.setEventId(event.getId());
        comment.setStatus(CommentStatus.OPEN);
        return toDto(commentsRepository.save(comment), creator);
    }

    @Override
    public CommentDto updateCommentPrivate(NewCommentDto newCommentDto, UpdateCommentParam param) {
        Comment checkedComment = checkCommentAvailability(param);
        checkedComment.setText(newCommentDto.getText());
        UserShortDto creator = getUser(checkedComment.getCreatorId());
        if (param.getStatus() != null) {
            checkedComment.setStatus(checkCreatorCommentStatus(param.getStatus()));
        }
        return toDto(commentsRepository.save(checkedComment), creator);
    }

    @Override
    public List<CommentDto> getCommentPrivate(GetCommentsParam param, Long userId) {
        List<Comment> commentList = commentsRepository.findByEventIdAndCreatorIdOrderByCreateTimeDesc(param.getEventId(),
                userId, param.toPage());
        Map<Long,UserShortDto> userShortDtoMap = getUserShortDtoMap(commentList);
        return commentList.stream()
                .map(c -> toDto(c, userShortDtoMap))
                .toList();
    }

    @Override
    public CommentDto deleteCommentPrivate(UpdateCommentParam param) {
        Comment checkedComment = checkCommentAvailability(param);
        UserShortDto creator = getUser(checkedComment.getCreatorId());
        commentsRepository.delete(checkedComment);
        return toDto(checkedComment, creator);
    }

    // Admin
    @Override
    public List<CommentDto> searchCommentAdmin(SearchCommentParam param, CommentStatus status) {
        Specification<Comment> spec = CommentSpecification.byParams(param, status);
        List<Comment> commentList = commentsRepository.findAll(spec, param.toPage()).getContent();
        Map<Long,UserShortDto> userShortDtoMap = getUserShortDtoMap(commentList);
        return commentList.stream()
                .map(c -> toDto(c, userShortDtoMap))
                .toList();
    }

    @Override
    public CommentDto updateCommentAdmin(Long commentId, CommentStatus commentStatus) {
        Comment comment = getComment(commentId);
        UserShortDto creator = getUser(comment.getCreatorId());
        // можно модерировать только комментарии со статусом OPEN
        if (comment.getStatus().equals(CommentStatus.HIDDEN))
            throw new ValidationException(String.format("Комментарий еще не опубликован, status = %s",
                    comment.getStatus()));
        comment.setStatus(checkAdminCommentStatus(commentStatus));
        return toDto(commentsRepository.save(comment), creator);
    }

    // вспомогательные методы
    private EventShortDto getEvent(Long eventId) {
        return eventClient.getShortEventById(eventId);
    }

    private UserShortDto getUser(Long commentatorId) {
        return userClient.getShortUserById(commentatorId);
    }

    private Comment getComment(Long commentId) {
        return commentsRepository.findById(commentId).orElseThrow(
                () -> new NotFoundException(String.format("Комментарий с ID %s не найден",
                        commentId)));
    }

    // метод для получения мапы объектов UserShortDto
    private Map<Long, UserShortDto> getUserShortDtoMap(List<Comment> commentList) {
        List<Long> creatorIds = commentList.stream()
                .map(Comment::getCreatorId)
                .toList();
        return userClient.findByIdIn(creatorIds).stream()
                .collect(Collectors.toMap(UserShortDto::getId, Function.identity()));

    }

    // метод проверяет доступность комментария для изменения
    private Comment checkCommentAvailability(UpdateCommentParam param) {
        EventShortDto event = getEvent(param.getEventId());
        Comment comment = getComment(param.getCommentId());
        if (!comment.getEventId().equals(event.getId())) {
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
        UserShortDto commentator = getUser(param.getUserId());
        // Можно менять только свой комментарий
        if (!comment.getCreatorId().equals(commentator.getId())) {
            throw new ValidationException(
                    String.format("Можно менять только свои комментарии, " +
                                    "id польтзователя %s не равен id комментатора %s",
                            comment.getCreatorId(), commentator.getId()));
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
