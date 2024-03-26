package ru.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.StatsClient;
import ru.practicum.dao.EventRequestRepository;
import ru.practicum.dto.*;
import ru.practicum.dao.CategoryRepository;
import ru.practicum.dao.EventRepository;
import ru.practicum.dao.UserRepository;
import ru.practicum.mapper.EventMapper;
import ru.practicum.mapper.ParticipationRequestMapper;
import ru.practicum.model.*;
import ru.practicum.service.EventService;
import ru.practicum.specification.EventSpecification;
import ru.practicum.stats.StatsViewDto;
import ru.practicum.validation.exception.ConflictException;
import ru.practicum.validation.exception.NotFoundException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;

    private final UserRepository userRepository;

    private final CategoryRepository categoryRepository;

    private final EventRequestRepository eventRequestRepository;

    private final StatsClient client;

    private final EventMapper mapper;

    private final ParticipationRequestMapper requestMapper;


    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> findBy(Long initiatorId, Pageable pageable) {
        userRepository.findById(initiatorId)
                .orElseThrow(() -> new NotFoundException("User with id=" + initiatorId + " was not found"));
        List<Event> events = eventRepository.findByInitiatorId(initiatorId, pageable);

        List<Long> eventIds = events.stream()
                .map(Event::getId)
                .collect(Collectors.toList());

        Map<Long, Long> confirmedRequestsMap = getConfirmedRequestsByEventIds(eventIds);

        return events.stream()
                .map(event -> {
                    Long confirmedRequests = confirmedRequestsMap.getOrDefault(event.getId(), 0L);
                    return mapper.toShortDto(event, null, confirmedRequests);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventInfoDto add(EventDto eventDto) {
        long categoryId = eventDto.getCategory();
        long initiatorId = eventDto.getInitiatorId();

        User initiator = userRepository.findById(initiatorId)
                .orElseThrow(() -> new NotFoundException("User with id=" + initiatorId + " was not found"));
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Category with id=" + categoryId + " was not found"));

        Event createEvent = mapper.toModel(initiator, category, eventDto);

        return mapper.toDto(eventRepository.save(createEvent));

    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto get(long initiatorId, long eventId) {
        userRepository.findById(initiatorId)
                .orElseThrow(() -> new NotFoundException("User with id=" + initiatorId + " was not found"));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));

        Long confirmedRequests = eventRequestRepository.countByEventIdAndStatus(event.getId(), RequestStatus.CONFIRMED);

        return mapper.toFullDto(event, null, confirmedRequests);
    }

    @Override
    @Transactional
    public EventInfoDto update(InitiatorEventUpdateDto initiatorEventUpdateDto) {
        long eventId = initiatorEventUpdateDto.getEventId();
        long initiatorId = initiatorEventUpdateDto.getInitiatorId();

        userRepository.findById(initiatorId)
                .orElseThrow(() -> new NotFoundException("User with id=" + initiatorId + " was not found"));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));

        if (event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("Only pending or canceled events can be changed");
        }

        if (initiatorEventUpdateDto.getCategory() != null) {
            Category category = categoryRepository.findById(initiatorEventUpdateDto.getCategory())
                    .orElseThrow(() -> new NotFoundException("Category with id=" + initiatorEventUpdateDto.getCategory() + " was not found"));
            event.setCategory(category);
        }

        updateEventFields(event, initiatorEventUpdateDto);

        return mapper.toDto(eventRepository.save(event));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getRequests(long initiatorId, long eventId) {
        userRepository.findById(initiatorId)
                .orElseThrow(() -> new NotFoundException("User with id=" + initiatorId + " was not found"));

        eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));

        List<ParticipationRequest> request = eventRequestRepository.findRequestsByInitiatorIdAndEventId(initiatorId, eventId);

        return request.stream()
                .map(requestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult update(EventUpdateRequestStatusDto eventUpdateRequestStatusDto) {
        long eventId = eventUpdateRequestStatusDto.getEventId();
        long initiatorId = eventUpdateRequestStatusDto.getInitiatorId();

        userRepository.findById(initiatorId)
                .orElseThrow(() -> new NotFoundException("User with id=" + initiatorId + " was not found"));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            throw new ConflictException("Confirmation of requests is not required for this event.");
        }

        List<ParticipationRequest> requests = eventRequestRepository.findRequestsByInitiatorIdAndEventId(initiatorId, eventId);

        long confirmedParticipationCount = requests.stream()
                .filter(request -> request.getStatus() == RequestStatus.CONFIRMED)
                .count();

        if (confirmedParticipationCount >= event.getParticipantLimit()) {
            throw new ConflictException("The event has reached the participation limit");
        }

        List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();
        List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();

        boolean isLimitReached = false;

        for (Long requestId : eventUpdateRequestStatusDto.getRequestIds()) {
            ParticipationRequest request = requests.stream()
                    .filter(req -> req.getId().equals(requestId))
                    .findFirst()
                    .orElseThrow(() -> new NotFoundException("Request with id=" + requestId + " was not found"));

            if (request.getStatus() != RequestStatus.PENDING) {
                throw new ConflictException("Request status can only be changed for requests in 'PENDING' status.");
            }

            if (!isLimitReached) {
                request.setStatus(eventUpdateRequestStatusDto.getStatus());

                if (request.getStatus() == RequestStatus.CONFIRMED) {
                    confirmedRequests.add(requestMapper.toDto(request));
                } else if (request.getStatus() == RequestStatus.REJECTED) {
                    rejectedRequests.add(requestMapper.toDto(request));
                }

                if (request.getStatus() == RequestStatus.CONFIRMED && ++confirmedParticipationCount >= event.getParticipantLimit()) {
                    isLimitReached = true;
                }
            } else {
                request.setStatus(RequestStatus.REJECTED);
                rejectedRequests.add(requestMapper.toDto(request));
            }
        }

        eventRequestRepository.saveAll(requests);

        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult();
        result.setConfirmedRequests(confirmedRequests);
        result.setRejectedRequests(rejectedRequests);

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventFullDto> findBy(EventFilterDto adminFilter, Pageable pageable) {

        List<Event> events = eventRepository.findAll(new EventSpecification(adminFilter), pageable).getContent();

        List<Long> eventIds = events.stream()
                .map(Event::getId)
                .collect(Collectors.toList());

        Map<Long, Long> confirmedRequestsMap = getConfirmedRequestsByEventIds(eventIds);


        return events.stream()
                .map(event -> {
                    Long confirmedRequests = confirmedRequestsMap.getOrDefault(event.getId(), 0L);
                    return mapper.toFullDto(event, null, confirmedRequests);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventFullDto update(EventUpdateAdminRequestDto updateEventAdminRequest) {

        long eventId = updateEventAdminRequest.getId();

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));

        if (updateEventAdminRequest.getStateAction() != null) {
            if (updateEventAdminRequest.getStateAction().equals(StateAction.PUBLISH_EVENT)) {
                if (!event.getState().equals(EventState.PENDING)) {
                    throw new ConflictException("Cannot publish the event because it's not in the right state: PENDING");
                }
            }
            if (updateEventAdminRequest.getStateAction().equals(StateAction.REJECT_EVENT)) {
                if (event.getState().equals(EventState.PUBLISHED)) {
                    throw new ConflictException("Cannot reject the event because it's already published");
                }
            }
        }

        updateEvent(event, updateEventAdminRequest);

        Long confirmedRequests = eventRequestRepository.countByEventIdAndStatus(event.getId(), RequestStatus.CONFIRMED);


        return mapper.toFullDto(eventRepository.save(event), null, confirmedRequests);

    }

    @Override
    public List<EventShortDto> getBy(EventFilterDto publicFilter, Pageable pageable) {

        Sort.Direction direction = Sort.Direction.ASC;
        if (publicFilter.getSort() == EventFilterDto.Sort.EVENT_DATE) {
            pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(direction, "eventDate"));
        }

        Page<Event> events = eventRepository.findAll(new EventSpecification(publicFilter), pageable);

        List<Long> eventIds = events.stream()
                .map(Event::getId)
                .collect(Collectors.toList());

        Map<Long, Long> confirmedRequestsMap = getConfirmedRequestsByEventIds(eventIds);

        List<Event> eventList = events.getContent();
        Map<Long, Long> viewsMap = views(eventList);

        if (publicFilter.getSort() == EventFilterDto.Sort.VIEWS) {
            eventList.sort(Comparator.comparingLong(event -> viewsMap.getOrDefault(event.getId(), 0L)));
        }

        return eventList.stream()
                .map(event -> {
                    Long confirmedRequests = confirmedRequestsMap.getOrDefault(event.getId(), 0L);
                    Long views = viewsMap.getOrDefault(event.getId(), 0L);

                    return mapper.toShortDto(event, views, confirmedRequests);
                })
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto findBy(long eventId) {

        Event event = eventRepository.findPublishedEvents(eventId, EventState.PUBLISHED)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));

        Long confirmedRequests = eventRequestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);

        Map<Long, Long> viewsMap = views(List.of(event));

        Long views = viewsMap.getOrDefault(eventId, 0L);

        return mapper.toFullDto(event, views, confirmedRequests);
    }

    private Map<Long, Long> getConfirmedRequestsByEventIds(List<Long> eventIds) {

        List<EventConfirmedRequestsInfo> results = eventRequestRepository.countConfirmedRequestsByEventIds(eventIds);

        Map<Long, Long> confirmedRequestsMap = new HashMap<>();
        for (EventConfirmedRequestsInfo result : results) {
            confirmedRequestsMap.put(result.getEventId(), result.getConfirmedRequests());
        }

        return confirmedRequestsMap;
    }

    private Map<Long, Long> views(List<Event> events) {
        List<String> eventPaths = events.stream()
                .map(event -> "/events/" + event.getId())
                .collect(Collectors.toList());

        StatsRequestParams statsRequestParams = new StatsRequestParams();
        LocalDateTime start = events.stream()
                .map(Event::getCreateOn)
                .min(LocalDateTime::compareTo)
                .orElse(LocalDateTime.now());
        statsRequestParams.setStart(start);
        statsRequestParams.setEnd(LocalDateTime.now());
        statsRequestParams.setUris(eventPaths);
        statsRequestParams.setUnique(true);

        List<StatsViewDto> views = client.get(statsRequestParams);

        return views.stream()
                .collect(Collectors.toMap(
                        view -> Long.parseLong(view.getUri().substring(view.getUri().lastIndexOf('/') + 1)),
                        StatsViewDto::getHits
                ));
    }


    private void updateEventFields(Event event, InitiatorEventUpdateDto initiatorEventUpdateDto) {
        if (initiatorEventUpdateDto.getAnnotation() != null) {
            event.setAnnotation(initiatorEventUpdateDto.getAnnotation());
        }
        if (initiatorEventUpdateDto.getDescription() != null) {
            event.setDescription(initiatorEventUpdateDto.getDescription());
        }
        if (initiatorEventUpdateDto.getEventDate() != null) {
            event.setEventDate(initiatorEventUpdateDto.getEventDate());
        }
        if (initiatorEventUpdateDto.getLocation() != null) {
            event.setLocation(initiatorEventUpdateDto.getLocation());
        }
        if (initiatorEventUpdateDto.getPaid() != null) {
            event.setPaid(initiatorEventUpdateDto.getPaid());
        }
        if (initiatorEventUpdateDto.getParticipantLimit() != 0) {
            event.setParticipantLimit(initiatorEventUpdateDto.getParticipantLimit());
        }
        if (initiatorEventUpdateDto.getStateAction() != null) {
            switch (initiatorEventUpdateDto.getStateAction()) {
                case SEND_TO_REVIEW:
                    event.setState(EventState.PENDING);
                    break;
                case CANCEL_REVIEW:
                    event.setState(EventState.CANCELED);
                    break;
            }
        }
        if (initiatorEventUpdateDto.getTitle() != null) {
            event.setTitle(initiatorEventUpdateDto.getTitle());
        }
    }

    private void updateEvent(Event event, EventUpdateAdminRequestDto updateEvent) {
        if (updateEvent.getAnnotation() != null) {
            event.setAnnotation(updateEvent.getAnnotation());
        }
        if (updateEvent.getDescription() != null) {
            event.setDescription(updateEvent.getDescription());
        }
        if (updateEvent.getEventDate() != null) {
            event.setEventDate(updateEvent.getEventDate());
        }
        if (updateEvent.getLocation() != null) {
            event.setLocation(updateEvent.getLocation());
        }
        if (updateEvent.getPaid() != null) {
            event.setPaid(updateEvent.getPaid());
        }
        if (updateEvent.getParticipantLimit() != 0) {
            event.setParticipantLimit(updateEvent.getParticipantLimit());
        }
        if (updateEvent.getStateAction() != null) {
            switch (updateEvent.getStateAction()) {
                case PUBLISH_EVENT:
                    event.setState(EventState.PUBLISHED);
                    break;
                case REJECT_EVENT:
                    event.setState(EventState.CANCELED);
                    break;
            }
        }
        if (updateEvent.getTitle() != null) {
            event.setTitle(updateEvent.getTitle());
        }
    }
}
