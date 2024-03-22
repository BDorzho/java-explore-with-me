package ru.practicum.stats.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.stats.StatsViewDto;
import ru.practicum.stats.StatsCreateDto;
import ru.practicum.stats.model.Stats;

@Component
public class StatsMapper {

    public Stats toModel(StatsCreateDto statsCreateDto) {
        return new Stats(statsCreateDto.getId(),
                statsCreateDto.getApp(),
                statsCreateDto.getUri(),
                statsCreateDto.getIp(),
                statsCreateDto.getTimeStamp());
    }

    public StatsViewDto toDto(Stats stats, long count) {
        return new StatsViewDto(
                stats.getApp(),
                stats.getUri(),
                count
        );
    }


}
