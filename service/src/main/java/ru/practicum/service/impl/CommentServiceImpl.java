package ru.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dao.CommentRepository;
import ru.practicum.dao.EventRepository;
import ru.practicum.dao.EventRequestRepository;
import ru.practicum.dao.UserRepository;
import ru.practicum.dto.CommentDto;
import ru.practicum.dto.CommentUpdateDto;
import ru.practicum.mapper.CommentMapper;
import ru.practicum.model.*;
import ru.practicum.service.CommentService;
import ru.practicum.validation.exception.ConflictException;
import ru.practicum.validation.exception.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository repository;

    private final UserRepository userRepository;

    private final EventRepository eventRepository;

    private final EventRequestRepository eventRequestRepository;

    private final CommentMapper mapper;

    @Override
    @Transactional
    public CommentDto add(CommentDto comment) {
        long authorId = comment.getAuthorId();
        long eventId = comment.getEventId();

        User user = userRepository.findById(authorId)
                .orElseThrow(() -> new NotFoundException("User with id=" + authorId + " was not found"));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));

        boolean isUserParticipant = eventRequestRepository.existsByRequesterIdAndEventIdAndStatus(authorId, eventId, RequestStatus.CONFIRMED);

        if (!isUserParticipant) {
            throw new ConflictException("User is not a participant of the event");
        }

        Comment savedComment = repository.save(mapper.toModel(comment, event, user));

        return mapper.toDto(savedComment);
    }

    @Override
    @Transactional(readOnly = true)
    public CommentDto get(long authorId, long eventId) {
        userRepository.findById(authorId)
                .orElseThrow(() -> new NotFoundException("User with id=" + authorId + " was not found"));

        eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));

        Comment comment = repository.findByEventIdAndAuthorId(eventId, authorId)
                .orElseThrow(() -> new NotFoundException("Comment not found for userId=" + authorId + " and eventId=" + eventId));

        return mapper.toDto(comment);
    }

    @Override
    @Transactional
    public CommentDto update(CommentUpdateDto commentUpdateDto) {
        Comment existingComment = repository.findById(commentUpdateDto.getId())
                .orElseThrow(() -> new NotFoundException("Comment with id=" + commentUpdateDto.getId() + " was not found"));

        existingComment.setText(commentUpdateDto.getText());

        return mapper.toDto(repository.save(existingComment));
    }

    @Override
    @Transactional
    public void delete(long commentId) {
        repository.deleteById(commentId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> getBy(long eventId, Pageable pageable) {
        List<Comment> commentPage = repository.findByEventId(eventId, pageable);
        return commentPage.stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }
}
