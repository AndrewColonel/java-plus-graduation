package ru.practicum.requests.dto.ext;

public interface BaseDto {
    void setViews(Long views);

    void setConfirmedRequests(Long confirmedRequests);

    Long getId();
}
