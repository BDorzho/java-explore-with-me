package ru.practicum.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.dto.*;


import java.util.List;

public interface EventService {
    List<EventShortDto> getInitiatorEvents(Long userId, Pageable pageable);

    EventInfoDto add(Long userId, EventDto eventDto);

    EventFullDto getEventDetails(Long userId, Long eventId);

    EventInfoDto update(Long userId, Long eventId, UpdateEventUserDto updateEventUserDto);

    List<ParticipationRequestDto> getRequests(Long userId, Long eventId);

    EventRequestStatusUpdateResult updateRequestStatus(Long userId, Long eventId, UpdateEventRequestStatusDto updateEventRequestStatusDto);

    List<EventFullDto> findAdminEvents(EventFilterDto filter, Pageable pageable);

    EventFullDto updateAdminEvent(Long eventId, UpdateEventAdminRequestDto updateEventAdminRequest);

    List<EventShortDto> findPublic(EventFilterDto filter, Pageable pageable);

    EventFullDto findByIdPublic(Long id);
}
