package ru.practicum.requests.model;

import jakarta.persistence.*;
import lombok.Data;
import ru.practicum.event.model.Event;
import ru.practicum.user.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "participation_requests")
@Data
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id", nullable = false)
    private User requester;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RequestStatus status = RequestStatus.PENDING;

    private LocalDateTime created;

    public enum RequestStatus {
        PENDING, CONFIRMED, REJECTED, CANCELED
    }

    public Long getEventId() {
        return event.getId();
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