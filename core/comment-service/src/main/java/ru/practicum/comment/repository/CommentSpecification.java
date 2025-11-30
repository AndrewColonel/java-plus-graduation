package ru.practicum.comment.repository;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import ru.practicum.comment.dto.SearchCommentParam;
import ru.practicum.comment.model.Comment;
import ru.practicum.comment.model.CommentStatus;

public class CommentSpecification {

    public static Specification<Comment> byParams(SearchCommentParam param, CommentStatus status) {

        return (root, query, cb) -> {
            Predicate predicate = cb.conjunction();

            if (param.getRangeStart() != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("created"), param.getRangeStart()));
            }

            if (param.getRangeEnd() != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("created"), param.getRangeEnd()));
            }

            predicate = cb.and(predicate, root.get("status").in(status));

            predicate = cb.and(predicate, root.get("event").get("id").in(param.getEventId()));

            return predicate;
        };

    }
}
