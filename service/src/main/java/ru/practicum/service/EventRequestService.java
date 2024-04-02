package ru.practicum.service;

import ru.practicum.dto.ParticipationRequestDto;

import java.util.List;

public interface EventRequestService {
    List<ParticipationRequestDto> getById(long userId);

    ParticipationRequestDto add(long userId, long eventId);

    ParticipationRequestDto cancel(long userId, long requestId);
}
