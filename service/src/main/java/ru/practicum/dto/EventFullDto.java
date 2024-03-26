package ru.practicum.dto;

import lombok.*;
import ru.practicum.model.EventState;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventFullDto {

    private Long id;

    private String title;

    private String annotation;

    private CategoryDto category;

    private Boolean paid;

    private String eventDate;

    private UserShortDto initiator;

    private Long views;

    private Long confirmedRequests;

    private String description;

    private int participantLimit;

    private EventState state;

    private String createdOn;

    private String publishedOn;

    private Location location;

    private boolean requestModeration;

}

