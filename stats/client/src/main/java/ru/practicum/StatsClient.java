package ru.practicum;

import ru.practicum.dto.StatsRequestParams;

import java.util.List;

public interface StatsClient {

    void add(StatsCreateDto statsCreateDto);

    List<StatsViewDto> get(StatsRequestParams params);

}
