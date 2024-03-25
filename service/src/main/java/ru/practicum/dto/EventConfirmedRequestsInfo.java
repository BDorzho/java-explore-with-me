package ru.practicum.dto;

import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventConfirmedRequestsInfo {
    private Long eventId;
    private Long confirmedRequests;
}

