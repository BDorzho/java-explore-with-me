package ru.practicum.stats.service;

import ru.practicum.stats.StatsViewDto;
import ru.practicum.stats.StatsCreateDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {
    void add(StatsCreateDto statsCreateDto);

    List<StatsViewDto> get(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);
}
