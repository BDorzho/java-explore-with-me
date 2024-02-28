package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StatsRequestParams {
    private LocalDateTime start;
    private LocalDateTime end;
    private List<String> uris;
    private boolean unique;
}
