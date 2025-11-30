package ru.practicum.comment.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.practicum.comment.model.Comment;
import ru.practicum.comment.model.CommentStatus;

import java.util.List;

public interface CommentsRepository extends JpaRepository<Comment, Long>, JpaSpecificationExecutor<Comment> {

    List<Comment> findByEventIdAndStatusOrderByCreateTimeDesc(Long eventId, CommentStatus status, Pageable pageable);

    List<Comment> findByEventIdAndCreatorIdOrderByCreateTimeDesc(Long eventId, Long userId, Pageable pageable);

}
