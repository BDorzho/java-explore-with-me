package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.StatsViewDto;
import ru.practicum.StatsCreateDto;
import ru.practicum.service.StatsService;


import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@Slf4j
public class StatsController {

    private final StatsService service;

    @PostMapping("/hit")
    public void add(@Valid @RequestBody StatsCreateDto statsCreateDto) {
        log.info("Запись информации о посещении");
        statsCreateDto.setTimeStamp(LocalDateTime.now());
        service.add(statsCreateDto);
        log.info("Запись успешна сохранена");
    }

    @GetMapping("/stats")
    public List<StatsViewDto> get(@RequestParam("start") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                  @RequestParam("end") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                  @RequestParam(value = "uris", required = false) List<String> uris,
                                  @RequestParam(value = "unique", defaultValue = "false") boolean unique) {
        log.info("Запрос статистики за период с {} по {}", start, end);
        List<StatsViewDto> statsViewDto = service.get(start, end, uris, unique);
        log.info("Запрос получен");
        return statsViewDto;
    }
}
