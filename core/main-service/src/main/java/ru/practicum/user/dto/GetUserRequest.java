package ru.practicum.user.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Objects;

@Data
@NoArgsConstructor
public class GetUserRequest {
    private List<Long> ids;
    private Integer from;
    private Integer size;
    private Pageable pageable;

    public static GetUserRequest of(
            List<Long> ids,
            Integer from,
            Integer size) {
        GetUserRequest getUserRequest = new GetUserRequest();
        getUserRequest.setIds(Objects.nonNull(ids) ? ids : List.of());
        getUserRequest.setFrom(from);
        getUserRequest.setSize(size);

        getUserRequest.setPageable(PageRequest.of(from / size, size));

        return getUserRequest;
    }
}
