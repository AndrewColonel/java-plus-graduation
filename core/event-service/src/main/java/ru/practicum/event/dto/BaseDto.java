package ru.practicum.event.dto;

public interface BaseDto {
    void setViews(Long views);

    void setConfirmedRequests(Long confirmedRequests);

    Long getId();
}
