package ru.practicum.analyzer.dal.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "similarities")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Similarity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "event1", nullable = false)
    private Long event1;
    @Column(name = "event2", nullable = false)
    private Long event2;
    @Column(name = "similarity", nullable = false)
    private Double similarity;
    @Column(name = "ts", nullable = false)
    private LocalDateTime ts;
}


