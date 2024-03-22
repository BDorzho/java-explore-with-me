package ru.practicum.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.model.EventState;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder(toBuilder = true)
public class EventFilterDto {

    private List<Long> users;

    private List<EventState> states;

    private List<Long> categories;

    private LocalDateTime rangeStart;

    private LocalDateTime rangeEnd;

    private String text;

    private Boolean paid;

    private Boolean onlyAvailable;

    private Sort sort;


    public static EventFilterDto fromQueryParams(List<Long> users,
                                                 List<String> states,
                                                 List<Long> categories,
                                                 LocalDateTime rangeStart,
                                                 LocalDateTime rangeEnd) {
        return EventFilterDto.builder()
                .users(users)
                .states(states != null ? states.stream().map(EventState::valueOf).collect(Collectors.toList()) : null)
                .categories(categories)
                .rangeStart(rangeStart)
                .rangeEnd(rangeEnd)
                .build();
    }

    public static EventFilterDto fromQueryParams(String text,
                                                 List<Long> categories,
                                                 Boolean paid,
                                                 LocalDateTime rangeStart,
                                                 LocalDateTime rangeEnd,
                                                 Boolean onlyAvailable,
                                                 String sort) {
        return EventFilterDto.builder()
                .text(text)
                .categories(categories)
                .paid(paid)
                .rangeStart(rangeStart)
                .rangeEnd(rangeEnd)
                .onlyAvailable(onlyAvailable)
                .sort(sort != null ? Sort.valueOf(sort.toUpperCase()) : null)
                .build();
    }

    public enum Sort {EVENT_DATE, VIEWS}

}
