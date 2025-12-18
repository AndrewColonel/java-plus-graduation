package ru.practicum.requests.dto.ext;

public interface BaseDto {
    void setRating(Double rating);

    void setConfirmedRequests(Long confirmedRequests);

    Long getId();
}
