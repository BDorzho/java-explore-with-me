package ru.practicum.controller.priv;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.*;
import ru.practicum.service.EventService;
import ru.practicum.validation.OnCreate;
import ru.practicum.validation.OnUpdate;
import ru.practicum.validation.validators.Validation;

import java.util.List;

@RestController
@Validated
@RequestMapping("/users/{userId}/events")
@Slf4j
@RequiredArgsConstructor
public class EventPrivateController {

    private final EventService eventService;

    private final Validation validation;

    @GetMapping
    public List<EventShortDto> get(@PathVariable Long userId,
                                   @RequestParam(defaultValue = "0") int from,
                                   @RequestParam(defaultValue = "10") int size) {

        log.info("Получение событий, добавленных текущим пользователем");
        Pageable pageable = PageRequest.of(from / size, size);
        List<EventShortDto> userEvents = eventService.findBy(userId, pageable);
        log.info("События найдены");

        return userEvents;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventInfoDto add(@PathVariable Long userId,
                            @Validated(OnCreate.class) @RequestBody EventDto eventDto) {

        log.info("Добавление нового события");
        validation.date(eventDto.getEventDate());
        EventInfoDto createdEvent = eventService.add(userId, eventDto);
        log.info("Событие добавлено");

        return createdEvent;
    }

    @GetMapping("/{eventId}")
    public EventFullDto getDetails(@PathVariable Long userId,
                                   @PathVariable Long eventId) {

        log.info("Получение полной информации о событии добавленном текущим пользователем");
        EventFullDto eventInfoDto = eventService.get(userId, eventId);
        log.info("Событие найдено");

        return eventInfoDto;
    }

    @PatchMapping("/{eventId}")
    public EventInfoDto update(@PathVariable Long userId,
                               @PathVariable Long eventId,
                               @Validated(OnUpdate.class) @RequestBody EventUpdateUserDto eventUpdateUserDto) {

        log.info("Изменение события добавленного текущим пользователем");

        validation.date(eventUpdateUserDto.getEventDate());

        EventInfoDto updatedEvent = eventService.update(userId, eventId, eventUpdateUserDto);
        log.info("Событие обновлено");

        return updatedEvent;
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> get(@PathVariable Long userId,
                                             @PathVariable Long eventId) {

        log.info("Получение информации о запросах на участие в событии текушего пользователя");
        List<ParticipationRequestDto> eventRequests = eventService.getRequests(userId, eventId);
        log.info("Найдены запросы на участие");
        return eventRequests;
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult update(@PathVariable Long userId,
                                                 @PathVariable Long eventId,
                                                 @Validated(OnUpdate.class) @RequestBody EventUpdateRequestStatusDto eventUpdateRequestStatusDto) {

        log.info("Изменение статуса(подтверждена/отменена) заявок на участие в событии текушего пользователя");
        EventRequestStatusUpdateResult updatedRequests = eventService.update(userId, eventId, eventUpdateRequestStatusDto);
        log.info("Статус заявок изменен");

        return updatedRequests;
    }


}
