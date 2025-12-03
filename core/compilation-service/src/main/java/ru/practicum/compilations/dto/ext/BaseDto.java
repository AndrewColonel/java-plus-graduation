package ru.practicum.compilations.dto.ext;

public interface BaseDto {
    void setViews(Long views);

    void setConfirmedRequests(Long confirmedRequests);

    Long getId();
}
