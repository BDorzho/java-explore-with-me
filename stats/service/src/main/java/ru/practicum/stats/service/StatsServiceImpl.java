package ru.practicum.stats.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.stats.dao.StatsRepository;
import ru.practicum.stats.mapper.StatsMapper;
import ru.practicum.stats.StatsViewDto;
import ru.practicum.stats.StatsCreateDto;

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

        validateDate(start, end);

        List<StatsViewDto> result;

        if (unique) {
            result = repository.findByUniqueIp(start, end, uris);
        } else {
            result = repository.findByTimestampBetweenAndUriIn(start, end, uris);
        }
        return result;
    }

    private void validateDate(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null || start.isAfter(end)) {
            throw new IllegalArgumentException("Invalid date range. Start date should not be after end date and both dates should not be empty.");
        }
    }
}