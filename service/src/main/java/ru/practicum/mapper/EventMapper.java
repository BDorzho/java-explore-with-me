package ru.practicum.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.dto.*;
import ru.practicum.model.Category;
import ru.practicum.model.Event;
import ru.practicum.model.EventState;
import ru.practicum.model.User;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Component
public class EventMapper {

    private static final DateTimeFormatter formatter = DateTimeFormatter
            .ofPattern("yyyy-MM-dd hh:mm:ss")
            .withZone(ZoneOffset.UTC);


    public Event toModel(User initiator, Category category, EventDto eventDto) {
        return new Event(eventDto.getId(),
                eventDto.getTitle(),
                eventDto.getAnnotation(),
                category,
                eventDto.getPaid(),
                eventDto.getEventDate(),
                initiator,
                eventDto.getDescription(),
                eventDto.getParticipantLimit(),
                EventState.PENDING,
                LocalDateTime.now(),
                eventDto.getLocation(),
                eventDto.getRequestModeration());
    }

    public EventInfoDto toDto(Event event) {
        return new EventInfoDto(event.getId(), event.getTitle(),
                event.getAnnotation(),
                new CategoryDto(event.getCategory().getId(), event.getCategory().getName()),
                event.getPaid(),
                formatter.format(event.getEventDate()),
                new UserShortDto(event.getInitiator().getId(), event.getInitiator().getName()),
                event.getDescription(),
                event.getParticipantLimit(),
                event.getState(),
                formatter.format(event.getCreateOn()),
                event.getLocation(),
                event.getRequestModeration());
    }

    public EventShortDto toShortDto(Event event, Long views, Long confirmedRequests) {
        return new EventShortDto(event.getId(),
                event.getTitle(),
                event.getAnnotation(),
                new CategoryDto(event.getCategory().getId(), event.getCategory().getName()),
                event.getPaid(),
                formatter.format(event.getEventDate()),
                new UserShortDto(event.getInitiator().getId(), event.getInitiator().getName()),
                views,
                confirmedRequests);
    }

    public EventFullDto toFullDto(Event event, Long views, Long confirmedRequests) {
        String publishedOn = null;

        if (event.getState() == EventState.PUBLISHED) {
            publishedOn = formatter.format(LocalDateTime.now());
        }
        return new EventFullDto(event.getId(),
                event.getTitle(),
                event.getAnnotation(),
                new CategoryDto(event.getCategory().getId(), event.getCategory().getName()),
                event.getPaid(),
                formatter.format(event.getEventDate()),
                new UserShortDto(event.getInitiator().getId(), event.getInitiator().getName()),
                views,
                confirmedRequests,
                event.getDescription(),
                event.getParticipantLimit(),
                event.getState(),
                formatter.format(event.getCreateOn()),
                publishedOn,
                event.getLocation(),
                event.getRequestModeration());
    }
}
