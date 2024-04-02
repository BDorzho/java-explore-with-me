package ru.practicum.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.dto.CommentCreateDto;
import ru.practicum.dto.CommentDto;
import ru.practicum.dto.CommentUpdateDto;

import java.util.List;

public interface CommentService {
    CommentDto add(CommentCreateDto comment);

    CommentDto get(long userId, long eventId);

    CommentDto update(CommentUpdateDto commentUpdateDto);

    void delete(long commentId);

    List<CommentDto> getBy(long eventId, Pageable pageable);
}
