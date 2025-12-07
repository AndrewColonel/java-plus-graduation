package ru.practicum.comment.dto;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetCommentsParam {
    @Positive
    private Long eventId;
    @PositiveOrZero
    @Builder.Default
    private Integer from = 0;
    @Positive
    @Builder.Default
    private Integer size = 10;

    public Pageable toPage() {
        return PageRequest.of(from > 0 ? from / size : 0, size);
    }

}
