package ru.practicum.event.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import ru.practicum.event.model.Location;
import ru.practicum.event.model.State;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "events")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String annotation;

    @Column(name = "category_id")
    private Long category;

    @CreationTimestamp
    private LocalDateTime createdOn;

    private String description;

    private LocalDateTime eventDate;

    @Column(name = "initiator_id")
    private Long initiatorId;

    @Embedded
    private Location location;

    private Boolean paid;

    private Integer participantLimit;

    private LocalDateTime publishedOn;

    private Boolean requestModeration;

    @Enumerated(EnumType.STRING)
    private State state;

    private String title;

}
