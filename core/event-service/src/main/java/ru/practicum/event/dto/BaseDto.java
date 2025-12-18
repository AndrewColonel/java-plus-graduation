package ru.practicum.event.dto;

public interface BaseDto {
    void setRating(Double rating);

    void setConfirmedRequests(Long confirmedRequests);

    Long getId();
}
