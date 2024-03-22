package ru.practicum.controller.adm;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.EventFilterDto;
import ru.practicum.dto.EventFullDto;
import ru.practicum.dto.UpdateEventAdminRequestDto;
import ru.practicum.service.EventService;
import ru.practicum.validation.OnUpdate;
import ru.practicum.validation.validators.Validation;

import java.time.LocalDateTime;
import java.util.List;

@Validated
@RestController
@RequestMapping("/admin/events")
@Slf4j
@RequiredArgsConstructor
public class EventAdminController {

    private final EventService service;

    private final Validation validation;


    @GetMapping
    public List<EventFullDto> findEvents(@RequestParam(required = false) List<Long> users,
                                         @RequestParam(required = false) List<String> states,
                                         @RequestParam(required = false) List<Long> categories,
                                         @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                         @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                         @RequestParam(required = false, defaultValue = "0") int from,
                                         @RequestParam(required = false, defaultValue = "10") int size) {

        log.info("Поиск событий");

        Pageable pageable = PageRequest.of(from / size, size);

        EventFilterDto filter = EventFilterDto.fromQueryParams(users, states, categories, rangeStart, rangeEnd);

        List<EventFullDto> events = service.findAdminEvents(filter, pageable);

        log.info("Событии найдены");

        return events;
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEvent(@PathVariable Long eventId,
                                    @Validated(OnUpdate.class) @RequestBody UpdateEventAdminRequestDto updateEventAdminRequest) {

        log.info("Редактирование данных события и его статуса (отклонения/публикация)");

        validation.date(updateEventAdminRequest.getEventDate());

        EventFullDto updateEvent = service.updateAdminEvent(eventId, updateEventAdminRequest);

        log.info("Событие успешно отредактировано");

        return updateEvent;
    }


}
