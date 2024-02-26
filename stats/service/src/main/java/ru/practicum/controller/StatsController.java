package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.GetCommonDto;
import ru.practicum.SaveCommonDto;
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
    public void add(@Valid @RequestBody SaveCommonDto saveCommonDto) {
        log.info("Запись информации о посещении");
        saveCommonDto.setTimeStamp(LocalDateTime.now());
        service.add(saveCommonDto);
        log.info("Запись успешна сохранена");
    }

    @GetMapping("/stats")
    public List<GetCommonDto> get(@RequestParam("start") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                  @RequestParam("end") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                  @RequestParam(value = "uris", required = false) List<String> uris,
                                  @RequestParam(value = "unique", defaultValue = "false") boolean unique) {
        log.info("Запрос статистики за период с {} по {}", start, end);
        List<GetCommonDto> getCommonDto = service.get(start, end, uris, unique);
        log.info("Запрос получен");
        return getCommonDto;
    }
}
