package ru.practicum.event.dto.ext;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class EventRequestStatusUpdateResult {
    private List<RequestDto> confirmedRequests;

    private List<RequestDto> rejectedRequests;
}