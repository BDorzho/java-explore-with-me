package ru.practicum.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.GetCommonDto;
import ru.practicum.SaveCommonDto;
import ru.practicum.model.Stats;

@Component
public class StatsMapper {

    public Stats toModel(SaveCommonDto saveCommonDto) {
        return new Stats(saveCommonDto.getId(),
                saveCommonDto.getApp(),
                saveCommonDto.getUri(),
                saveCommonDto.getIp(),
                saveCommonDto.getTimeStamp());
    }

    public GetCommonDto toDto(Stats stats, long count) {
        return new GetCommonDto(
                stats.getApp(),
                stats.getUri(),
                count
        );
    }


}
