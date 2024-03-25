package ru.practicum.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.dto.*;


import java.util.List;

public interface EventService {
    List<EventShortDto> findBy(Long initiatorId, Pageable pageable);

    EventInfoDto add(Long initiatorId, EventDto eventDto);

    EventFullDto get(Long initiatorId, Long eventId);

    EventInfoDto update(Long initiatorId, Long eventId, EventUpdateUserDto eventUpdateUserDto);

    List<ParticipationRequestDto> getRequests(Long initiatorId, Long eventId);

    EventRequestStatusUpdateResult update(Long initiatorId, Long eventId, EventUpdateRequestStatusDto eventUpdateRequestStatusDto);

    List<EventFullDto> findBy(EventFilterDto adminFilter, Pageable pageable);

    EventFullDto update(Long eventId, EventUpdateAdminRequestDto updateEventAdminRequest);

    List<EventShortDto> getBy(EventFilterDto publicFilter, Pageable pageable);

    EventFullDto findBy(Long eventId);
}
