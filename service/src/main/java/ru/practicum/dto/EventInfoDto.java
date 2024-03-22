package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.model.EventState;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventInfoDto {

    private Long id;

    private String title;

    private String annotation;

    private CategoryDto category;

    private Boolean paid;

    private String eventDate;

    private UserShortDto initiator;

    private String description;

    private int participantLimit;

    private EventState state;

    private String createdOn;

    private Location location;

    private boolean requestModeration;


}
