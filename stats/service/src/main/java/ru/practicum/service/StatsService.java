package ru.practicum.service;

import ru.practicum.StatsViewDto;
import ru.practicum.StatsCreateDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {
    void add(StatsCreateDto statsCreateDto);

    List<StatsViewDto> get(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);
}
