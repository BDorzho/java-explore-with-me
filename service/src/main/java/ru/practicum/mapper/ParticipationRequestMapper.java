package ru.practicum.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.dto.ParticipationRequestDto;
import ru.practicum.model.Event;
import ru.practicum.model.ParticipationRequest;
import ru.practicum.model.RequestStatus;
import ru.practicum.model.User;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Component
public class ParticipationRequestMapper {

    private static final DateTimeFormatter formatter = DateTimeFormatter
            .ofPattern("yyyy-MM-dd hh:mm:ss")
            .withZone(ZoneOffset.UTC);


    public ParticipationRequest toModel(User requester, Event event, RequestStatus status) {
        ParticipationRequest participationRequest = new ParticipationRequest();
        participationRequest.setRequester(requester);
        participationRequest.setEvent(event);
        participationRequest.setStatus(status);
        participationRequest.setCreated(LocalDateTime.now());
        return participationRequest;
    }

    public ParticipationRequestDto toDto(ParticipationRequest model) {
        return new ParticipationRequestDto(model.getId(),
                model.getRequester().getId(),
                model.getEvent().getId(),
                model.getStatus(),
                formatter.format(model.getCreated()));
    }
}
