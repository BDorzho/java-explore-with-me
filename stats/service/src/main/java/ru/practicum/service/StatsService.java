package ru.practicum.service;

import ru.practicum.GetCommonDto;
import ru.practicum.SaveCommonDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {
    void add(SaveCommonDto saveCommonDto);

    List<GetCommonDto> get(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);
}
