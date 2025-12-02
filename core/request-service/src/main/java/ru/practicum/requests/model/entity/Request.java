package ru.practicum.requests.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "participation_requests")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id", nullable = false)
    private Long event;

    @Column(name = "requester_id", nullable = false)
    private Long requester;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private RequestStatus status = RequestStatus.PENDING;

    private LocalDateTime created;

    public enum RequestStatus {
        PENDING, CONFIRMED, REJECTED, CANCELED
    }

    public Long getEventId() {
        return event;
    }

    public boolean isPending() {
        return status == RequestStatus.PENDING;
    }

    public boolean isConfirmed() {
        return status == RequestStatus.CONFIRMED;
    }

    public void confirm() {
        if (!isPending()) {
            throw new IllegalStateException("Невозможно полдтвердить заявку, которая уже не в состоянии PENDING");
        }
        status = RequestStatus.CONFIRMED;
    }

    public void reject() {
        if (!isPending()) {
            throw new IllegalStateException("Невозможно отменить заявку, которая уже не в состоянии PENDING");
        }
        status = RequestStatus.REJECTED;
    }

    public void cancel() {
        if (!isPending()) {
            throw new IllegalStateException("Невозможно отменить заявку, которая уже не в состоянии PENDING");
        }
        status = RequestStatus.CANCELED;
    }

}