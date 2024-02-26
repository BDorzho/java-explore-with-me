package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.GetCommonDto;
import ru.practicum.SaveCommonDto;
import ru.practicum.dao.StatsRepository;
import ru.practicum.mapper.StatsMapper;
import ru.practicum.model.Stats;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final StatsRepository repository;

    private final StatsMapper mapper;

    @Transactional
    @Override
    public void add(SaveCommonDto saveCommonDto) {
        repository.save(mapper.toModel(saveCommonDto));
    }

    @Transactional(readOnly = true)
    @Override
    public List<GetCommonDto> get(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {

        List<Stats> statsList;
        if (uris != null && !uris.isEmpty()) {
            statsList = repository.findByTimestampBetweenAndUriIn(start, end, uris);
        } else {
            statsList = repository.findByTimestampBetween(start, end);
        }

        Map<String, Long> uriCounts = statsList.stream()
                .collect(Collectors.groupingBy(Stats::getUri, Collectors.counting()));

        Set<String> processedUris = new HashSet<>();

        List<GetCommonDto> result = statsList.stream()
                .filter(stat -> processedUris.add(stat.getUri()))
                .map(stat -> mapper.toDto(stat, uriCounts.get(stat.getUri())))
                .collect(Collectors.toList());

        if (unique) {
            Map<String, Stats> uniqueIp = new LinkedHashMap<>();
            for (Stats stat : statsList) {
                uniqueIp.put(stat.getIp(), stat);
            }

            result = uniqueIp.values().stream()
                    .map(stat -> mapper.toDto(stat, 1))
                    .collect(Collectors.toList());
        }

        result.sort(Comparator.comparingLong(dto -> -dto.getHits()));

        return result;
    }
}