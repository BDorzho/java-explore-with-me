package ru.practicum.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.dto.*;


import java.util.List;

public interface EventService {
    List<EventShortDto> findBy(Long initiatorId, Pageable pageable);

    EventInfoDto add(EventDto eventDto);

    EventFullDto get(long initiatorId, long eventId);

    EventInfoDto update(InitiatorEventUpdateDto initiatorEventUpdateDto);

    List<ParticipationRequestDto> getRequests(long initiatorId, long eventId);

    EventRequestStatusUpdateResult update(EventUpdateRequestStatusDto eventUpdateRequestStatusDto);

    List<EventFullDto> findBy(EventFilterDto adminFilter, Pageable pageable);

    EventFullDto update(EventUpdateAdminRequestDto updateEventAdminRequest);

    List<EventShortDto> getBy(EventFilterDto publicFilter, Pageable pageable);

    EventFullDto findBy(long eventId);
}
