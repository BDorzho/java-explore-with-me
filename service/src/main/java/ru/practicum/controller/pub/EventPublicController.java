package ru.practicum.controller.pub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.EventFilterDto;
import ru.practicum.dto.EventFullDto;
import ru.practicum.dto.EventShortDto;
import ru.practicum.service.EventService;
import ru.practicum.StatsClient;
import ru.practicum.stats.StatsCreateDto;
import ru.practicum.validation.exception.ValidationException;


import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/events")
@Slf4j
@RequiredArgsConstructor
public class EventPublicController {

    private final EventService eventService;

    private final StatsClient client;

    @GetMapping
    public List<EventShortDto> find(@RequestParam(required = false) String text,
                                    @RequestParam(required = false) List<Long> categories,
                                    @RequestParam(required = false) Boolean paid,
                                    @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                    @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                    @RequestParam(required = false, defaultValue = "false") Boolean onlyAvailable,
                                    @RequestParam(required = false) String sort,
                                    @RequestParam(required = false, defaultValue = "0") int from,
                                    @RequestParam(required = false, defaultValue = "10") int size,
                                    HttpServletRequest request
    ) {
        log.info("Получение событий с возможностью фильтрации");

        StatsCreateDto statsCreateDto = new StatsCreateDto("ewm-main-service",
                request.getRequestURI(),
                request.getRemoteAddr(),
                LocalDateTime.now());
        client.add(statsCreateDto);

        if (rangeStart == null) {
            rangeStart = LocalDateTime.now();
        }

        if (rangeEnd != null && rangeEnd.isBefore(rangeStart)) {
            throw new ValidationException("Время окончания не может быть раньше времени начала");
        }


        Pageable pageable = PageRequest.of(from / size, size);

        EventFilterDto filter = EventFilterDto.fromQueryParams(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort);

        List<EventShortDto> events = eventService.findPublic(filter, pageable);

        log.info("События найдены");


        return events;
    }

    @GetMapping("/{id}")
    public EventFullDto getById(@PathVariable Long id, HttpServletRequest request) {

        log.info("Получение подробной информации об опубликованном событии по его идентификатору");

        StatsCreateDto statsCreateDto = new StatsCreateDto("ewm-main-service",
                request.getRequestURI(),
                request.getRemoteAddr(),
                LocalDateTime.now());
        client.add(statsCreateDto);

        EventFullDto event = eventService.findByIdPublic(id);

        log.info("Событие найдено");


        return event;
    }
}


