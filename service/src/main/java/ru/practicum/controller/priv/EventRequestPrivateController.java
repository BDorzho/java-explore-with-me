package ru.practicum.controller.priv;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.ParticipationRequestDto;
import ru.practicum.service.EventRequestService;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/requests")
@Slf4j
@RequiredArgsConstructor
public class EventRequestPrivateController {

    private final EventRequestService service;

    @GetMapping
    public List<ParticipationRequestDto> get(@PathVariable Long userId) {
        log.info("Получение информации о заявках текущего пользователя на участие в чужих событиях");
        List<ParticipationRequestDto> participationRequest = service.getById(userId);
        log.info("Найдены запросы на участие");
        return participationRequest;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto add(@PathVariable Long userId,
                                       @RequestParam Long eventId) {
        log.info("Добавление запроса от текущего пользователя на участие в событии");
        ParticipationRequestDto participationRequest = service.add(userId, eventId);
        log.info("Заявка создана");
        return participationRequest;
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto cancel(@PathVariable Long userId,
                                          @PathVariable Long requestId) {
        log.info("Отмена своего запроса на участие в событии");
        ParticipationRequestDto participationRequest = service.cancel(userId, requestId);
        log.info("Заявка отменена");
        return participationRequest;
    }
}


