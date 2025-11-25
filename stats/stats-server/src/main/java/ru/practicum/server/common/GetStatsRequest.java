package ru.practicum.server.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.server.exception.ValidationException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetStatsRequest {
    private LocalDateTime start;
    private LocalDateTime end;
    private List<String> uris;
    private Boolean unique;

    public static GetStatsRequest of(String start,
                                     String end,
                                     List<String> uris,
                                     String unique) {
        GetStatsRequest getStatsRequest = new GetStatsRequest();

        try {
            getStatsRequest.setStart(LocalDateTime.parse(start,
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        } catch (DateTimeParseException e) {
            throw new ValidationException(
                    String.format("Ошибка в параметре дата и время начала диапазона - start = %s", start));
        }

        try {
            getStatsRequest.setEnd(LocalDateTime.parse(end,
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        } catch (DateTimeParseException e) {
            throw new ValidationException(
                    String.format("Ошибка в параметре дата и время конца диапазона - end = %s", end));
        }

        if (getStatsRequest.getStart().isAfter(getStatsRequest.getEnd()))
            throw new ValidationException(
                    String.format("Дата начала диапазона %s после даты конца %s", start, end));

        try {
            getStatsRequest.setUnique(Boolean.valueOf(unique));
        } catch (IllegalArgumentException e) {
            throw new ValidationException(
                    String.format("Ошибка в параметре запроса только уникальных IP - unique = %s", unique));
        }

        if (Objects.isNull(uris)) uris = List.of();
        getStatsRequest.setUris(uris);

        return getStatsRequest;
    }
}
