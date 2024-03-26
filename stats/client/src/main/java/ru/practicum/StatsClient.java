package ru.practicum;

import ru.practicum.dto.StatsRequestParams;
import ru.practicum.stats.StatsCreateDto;
import ru.practicum.stats.StatsViewDto;

import java.util.List;


public interface StatsClient {

    void add(StatsCreateDto statsCreateDto);

    List<StatsViewDto> get(StatsRequestParams params);

}
