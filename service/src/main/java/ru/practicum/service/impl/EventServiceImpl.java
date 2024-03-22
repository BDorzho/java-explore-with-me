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
    public List<EventShortDto> getInitiatorEvents(Long userId, Pageable pageable) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));
        List<Event> events = eventRepository.findByInitiatorId(userId, pageable);

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
    public EventInfoDto add(Long userId, EventDto eventDto) {
        Long categoryId = eventDto.getCategory();
        User initiator = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Category with id=" + categoryId + " was not found"));

        Event createEvent = mapper.toModel(initiator, category, eventDto);

        return mapper.toDto(eventRepository.save(createEvent));

    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getEventDetails(Long userId, Long eventId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));

        Long confirmedRequests = eventRequestRepository.countByEventIdAndStatus(event.getId(), RequestStatus.CONFIRMED);

        return mapper.toFullDto(event, null, confirmedRequests);
    }

    @Override
    @Transactional
    public EventInfoDto update(Long userId, Long eventId, UpdateEventUserDto updateEventUserDto) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));

        if (event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("Only pending or canceled events can be changed");
        }

        if (updateEventUserDto.getCategory() != null) {
            Category category = categoryRepository.findById(updateEventUserDto.getCategory())
                    .orElseThrow(() -> new NotFoundException("Category with id=" + updateEventUserDto.getCategory() + " was not found"));
            event.setCategory(category);
        }

        updateEventFields(event, updateEventUserDto);

        return mapper.toDto(eventRepository.save(event));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getRequests(Long userId, Long eventId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));

        eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));

        List<ParticipationRequest> request = eventRequestRepository.findRequestsByInitiatorIdAndEventId(userId, eventId);

        return request.stream()
                .map(requestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult updateRequestStatus(Long userId, Long eventId, UpdateEventRequestStatusDto updateEventRequestStatusDto) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            throw new ConflictException("Confirmation of requests is not required for this event.");
        }

        List<ParticipationRequest> requests = eventRequestRepository.findRequestsByInitiatorIdAndEventId(userId, eventId);

        long confirmedParticipationCount = requests.stream()
                .filter(request -> request.getStatus() == RequestStatus.CONFIRMED)
                .count();

        if (confirmedParticipationCount >= event.getParticipantLimit()) {
            throw new ConflictException("The event has reached the participation limit");
        }

        List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();
        List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();

        boolean isLimitReached = false;

        for (Long requestId : updateEventRequestStatusDto.getRequestIds()) {
            ParticipationRequest request = requests.stream()
                    .filter(req -> req.getId().equals(requestId))
                    .findFirst()
                    .orElseThrow(() -> new NotFoundException("Request with id=" + requestId + " was not found"));

            if (request.getStatus() != RequestStatus.PENDING) {
                throw new ConflictException("Request status can only be changed for requests in 'PENDING' status.");
            }

            if (!isLimitReached) {
                request.setStatus(updateEventRequestStatusDto.getStatus());

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
    public List<EventFullDto> findAdminEvents(EventFilterDto filter, Pageable pageable) {

        List<Event> events = eventRepository.findAll(new EventSpecification(filter), pageable).getContent();

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
    public EventFullDto updateAdminEvent(Long eventId, UpdateEventAdminRequestDto updateEventAdminRequest) {

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
    public List<EventShortDto> findPublic(EventFilterDto filter, Pageable pageable) {

        Sort.Direction direction = Sort.Direction.ASC;
        if (filter.getSort() == EventFilterDto.Sort.EVENT_DATE) {
            pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(direction, "eventDate"));
        }

        Page<Event> events = eventRepository.findAll(new EventSpecification(filter), pageable);

        List<Long> eventIds = events.stream()
                .map(Event::getId)
                .collect(Collectors.toList());

        Map<Long, Long> confirmedRequestsMap = getConfirmedRequestsByEventIds(eventIds);

        List<Event> eventList = events.getContent();
        List<Long> setViews = views(eventList);

        if (filter.getSort() == EventFilterDto.Sort.VIEWS) {
            eventList.sort(Comparator.comparingLong(event -> setViews.get(eventList.indexOf(event))));
        }

        return eventList.stream()
                .map(event -> {
                    Long confirmedRequests = confirmedRequestsMap.getOrDefault(event.getId(), 0L);
                    Long views = setViews.get(eventList.indexOf(event));

                    return mapper.toShortDto(event, views, confirmedRequests);
                })
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto findByIdPublic(Long eventId) {

        Event event = eventRepository.findPublishedEvents(eventId, EventState.PUBLISHED)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));

        Long confirmedRequests = eventRequestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);

        List<Long> setViews = views(List.of(event));

        return mapper.toFullDto(event, setViews.get(0), confirmedRequests);
    }

    private List<Long> views(List<Event> events) {
        List<String> eventPaths = events.stream()
                .map(event -> "/events/" + event.getId())
                .collect(Collectors.toList());

        StatsRequestParams statsRequestParams = new StatsRequestParams();
        statsRequestParams.setStart(events.stream()
                .map(Event::getCreateOn)
                .min(LocalDateTime::compareTo)
                .orElse(LocalDateTime.now()));
        statsRequestParams.setEnd(LocalDateTime.now());
        statsRequestParams.setUris(eventPaths);
        statsRequestParams.setUnique(true);

        List<StatsViewDto> views = client.get(statsRequestParams);

        return events.stream()
                .map(event -> views.stream()
                        .filter(view -> view.getUri().endsWith("/" + event.getId()))
                        .mapToLong(StatsViewDto::getHits)
                        .findFirst()
                        .orElse(0L))
                .collect(Collectors.toList());
    }

    private Map<Long, Long> getConfirmedRequestsByEventIds(List<Long> eventIds) {

        List<Object[]> result = eventRequestRepository.countConfirmedRequestsByEventIds(eventIds);

        return result.stream()
                .collect(Collectors.toMap(
                        arr -> (Long) arr[0],
                        arr -> (Long) arr[1]
                ));
    }

    private void updateEventFields(Event event, UpdateEventUserDto updateEventUserDto) {
        if (updateEventUserDto.getAnnotation() != null) {
            event.setAnnotation(updateEventUserDto.getAnnotation());
        }
        if (updateEventUserDto.getDescription() != null) {
            event.setDescription(updateEventUserDto.getDescription());
        }
        if (updateEventUserDto.getEventDate() != null) {
            event.setEventDate(updateEventUserDto.getEventDate());
        }
        if (updateEventUserDto.getLocation() != null) {
            event.setLocation(updateEventUserDto.getLocation());
        }
        if (updateEventUserDto.getPaid() != null) {
            event.setPaid(updateEventUserDto.getPaid());
        }
        if (updateEventUserDto.getParticipantLimit() != 0) {
            event.setParticipantLimit(updateEventUserDto.getParticipantLimit());
        }
        if (updateEventUserDto.getStateAction() != null) {
            switch (updateEventUserDto.getStateAction()) {
                case SEND_TO_REVIEW:
                    event.setState(EventState.PENDING);
                    break;
                case CANCEL_REVIEW:
                    event.setState(EventState.CANCELED);
                    break;
            }
        }
        if (updateEventUserDto.getTitle() != null) {
            event.setTitle(updateEventUserDto.getTitle());
        }
    }

    private void updateEvent(Event event, UpdateEventAdminRequestDto updateEvent) {
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