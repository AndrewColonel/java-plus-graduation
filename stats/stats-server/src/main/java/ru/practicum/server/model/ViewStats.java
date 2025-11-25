package ru.practicum.server.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class ViewStats {

    private String app;
    private String uri;
    private Long hits;
}
