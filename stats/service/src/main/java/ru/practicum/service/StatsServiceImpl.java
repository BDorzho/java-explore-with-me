package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.StatsViewDto;
import ru.practicum.StatsCreateDto;
import ru.practicum.dao.StatsRepository;
import ru.practicum.mapper.StatsMapper;

import java.time.LocalDateTime;
import java.util.*;


@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final StatsRepository repository;

    private final StatsMapper mapper;

    @Transactional
    @Override
    public void add(StatsCreateDto statsCreateDto) {
        repository.save(mapper.toModel(statsCreateDto));
    }

    @Transactional(readOnly = true)
    @Override
    public List<StatsViewDto> get(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {

        List<StatsViewDto> result;

        if (unique) {
            result = repository.findByUniqueIp(start, end, uris);
        } else {
            result = repository.findByTimestampBetweenAndUriIn(start, end, uris);
        }
        return result;
    }
}