package ru.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dao.EventRequestRepository;
import ru.practicum.dao.EventRepository;
import ru.practicum.dao.UserRepository;
import ru.practicum.dto.ParticipationRequestDto;
import ru.practicum.mapper.ParticipationRequestMapper;
import ru.practicum.model.*;
import ru.practicum.service.EventRequestService;
import ru.practicum.validation.exception.ConflictException;
import ru.practicum.validation.exception.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventRequestServiceImpl implements EventRequestService {

    private final EventRequestRepository repository;

    private final UserRepository userRepository;

    private final EventRepository eventRepository;

    private final ParticipationRequestMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getById(long userId) {

        List<ParticipationRequest> request = repository.findByRequesterId(userId);


        return request.stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());

    }

    @Override
    @Transactional
    public ParticipationRequestDto add(long userId, long eventId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));

        checkParticipationRequest(user, event);

        RequestStatus status;
        if (event.getParticipantLimit() == 0) {
            status = RequestStatus.CONFIRMED;
        } else {
            status = event.getRequestModeration() ? RequestStatus.PENDING : RequestStatus.CONFIRMED;
        }

        ParticipationRequest request = repository.save(mapper.toModel(user, event, status));

        return mapper.toDto(request);
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancel(long userId, long requestId) {
        ParticipationRequest request = repository.findByIdAndRequesterId(requestId, userId)
                .orElseThrow(() -> new NotFoundException("Request with id " + requestId + " was not found"));

        request.setStatus(RequestStatus.CANCELED);
        repository.save(request);

        return mapper.toDto(request);
    }


    private void checkParticipationRequest(User user, Event event) {

        if (event.getState() != EventState.PUBLISHED) {
            throw new ConflictException("Participation is not allowed in an unpublished event");
        }

        if (user.getId().equals(event.getInitiator().getId())) {
            throw new ConflictException("The event initiator cannot request participation in their own event");
        }

        if (event.getParticipantLimit() != 0) {
            Long confirmedParticipationCount = repository.countByEventIdAndStatus(event.getId(), RequestStatus.CONFIRMED);
            if (confirmedParticipationCount != null) {
                if (confirmedParticipationCount >= event.getParticipantLimit()) {
                    throw new ConflictException("The event has reached the participation limit");
                }
            }
        }
    }
}
