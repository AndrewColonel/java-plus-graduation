package ru.practicum.event;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.RequestAdminParams;
import ru.practicum.event.model.RequestPublicParams;
import ru.practicum.event.model.State;

import java.time.LocalDateTime;
import java.util.List;

public class EventSpecification {

    public static Specification<Event> byParams(RequestAdminParams params) {
        return (root, query, cb) -> {
            Predicate predicate = cb.conjunction();

            List<Long> users = params.getUsers();
            if (users != null && !users.isEmpty() && !(users.size() == 1 && users.get(0) == 0L)) {
                predicate = cb.and(predicate, root.get("initiator").get("id").in(users));
            }

            if (params.getStates() != null && !params.getStates().isEmpty()) {
                predicate = cb.and(predicate, root.get("state").in(params.getStates()));
            }

            List<Long> categories = params.getCategories();
            if (categories != null && !categories.isEmpty() && !(categories.size() == 1 && categories.get(0) == 0L)) {
                predicate = cb.and(predicate, root.get("category").get("id").in(params.getCategories()));
            }

            if (params.getRangeStart() != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("eventDate"), params.getRangeStart()));
            }

            if (params.getRangeEnd() != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("eventDate"), params.getRangeEnd()));
            }

            return predicate;
        };
    }

    public static Specification<Event> byParams(RequestPublicParams params) {
        return (root, query, cb) -> {
            Predicate predicate = cb.conjunction();

            predicate = cb.and(predicate, cb.equal(root.get("state"), State.PUBLISHED));

            if (params.getText() != null && !params.getText().isEmpty()) {
                String pattern = "%" + params.getText().toLowerCase() + "%";
                Predicate annotationLike = cb.like(cb.lower(root.get("annotation")), pattern);
                Predicate descriptionLike = cb.like(cb.lower(root.get("description")), pattern);
                predicate = cb.and(predicate, cb.or(annotationLike, descriptionLike));
            }

            List<Long> categories = params.getCategories();
            if (categories != null && !categories.isEmpty() && !(categories.size() == 1 && categories.get(0) == 0L)) {
                predicate = cb.and(predicate, root.get("category").get("id").in(categories));
            }

            if (params.getPaid() != null) {
                predicate = cb.and(predicate, cb.equal(root.get("paid"), params.getPaid()));
            }

            LocalDateTime now = LocalDateTime.now();

            if (params.getRangeStart() != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("eventDate"), params.getRangeStart()));
            } else {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("eventDate"), now));
            }

            if (params.getRangeEnd() != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("eventDate"), params.getRangeEnd()));
            }

            if (params.getOnlyAvailable() != null && params.getOnlyAvailable()) {
                predicate = cb.and(predicate,
                        cb.or(
                                cb.equal(root.get("participantLimit"), 0),
                                cb.lessThan(root.get("confirmedRequests"), root.get("participantLimit"))
                        )
                );
            }

            return predicate;
        };
    }
}