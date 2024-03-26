package ru.practicum.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.dto.CommentDto;
import ru.practicum.model.Comment;
import ru.practicum.model.Event;
import ru.practicum.model.User;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Component
public class CommentMapper {

    private static final DateTimeFormatter formatter = DateTimeFormatter
            .ofPattern("yyyy-MM-dd hh:mm:ss")
            .withZone(ZoneOffset.UTC);


    public Comment toModel(CommentDto commentDto, Event event, User user) {
        return new Comment(commentDto.getId(),
                event,
                user,
                commentDto.getText(),
                LocalDateTime.now());
    }

    public CommentDto toDto(Comment comment) {
        return new CommentDto(comment.getId(),
                comment.getEvent().getId(),
                comment.getAuthor().getId(),
                comment.getText(),
                formatter.format(comment.getCreated()));
    }


}
