package ru.practicum.compilations;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import ru.practicum.event.model.Event;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "compilations")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Compilation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    @Size(max = 50, message = "Длинна наименования не больше 50 символов.")
    private String title;

    @Column(nullable = false)
    private Boolean pinned;

    @ManyToMany
    @JoinTable(
            name = "compilation_events",
            joinColumns = @JoinColumn(name = "compilation_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id")
    )
    @Builder.Default
    private Set<Event> events = new HashSet<>();


    /**
     * Обновляет данные подборки (кроме списка событий)
     *
     * @param title  новый заголовок
     * @param pinned новый статус закрепления
     */
    public void updateDetails(String title, Boolean pinned) {
        if (title != null) {
            this.title = title;
        }
        if (pinned != null) {
            this.pinned = pinned;
        }
    }

    /**
     * Полностью заменяет список событий в подборке
     *
     * @param newEvents новый набор событий
     */
    public void replaceEvents(Set<Event> newEvents) {
        this.events.clear();
        if (newEvents != null && !newEvents.isEmpty()) {
            this.events.addAll(newEvents);
        }
    }
}