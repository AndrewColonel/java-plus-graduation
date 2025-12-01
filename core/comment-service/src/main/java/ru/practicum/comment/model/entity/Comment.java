package ru.practicum.comment.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import ru.practicum.comment.model.CommentStatus;
import ru.practicum.user.model.entity.User;


import java.time.LocalDateTime;


@Entity
@Table(name = "comments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id", nullable = false)
    private Long event;

    @Column(name = "creator_id", nullable = false)
    private Long creator;

    @Column(name = "text", nullable = false)
    private String text;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private CommentStatus status;

    @Column(name = "createTime", nullable = false)
    @CreationTimestamp
    private LocalDateTime createTime;
}
