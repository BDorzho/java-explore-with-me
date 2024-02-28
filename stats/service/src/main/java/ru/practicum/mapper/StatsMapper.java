package ru.practicum.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.StatsViewDto;
import ru.practicum.StatsCreateDto;
import ru.practicum.model.Stats;

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
