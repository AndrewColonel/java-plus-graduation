package ru.practicum.analyzer.dal.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "interactions")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Interaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_id", nullable = false)
    private Long userId;
    @Column(name = "event_id", nullable = false)
    private  Long eventId;
    @Column(name = "rating", nullable = false)
    private Double rating;
    @Column(name = "ts", nullable = false)
    private LocalDateTime ts;
}
