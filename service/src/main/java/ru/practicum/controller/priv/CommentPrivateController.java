package ru.practicum.controller.priv;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.CommentCreateDto;
import ru.practicum.dto.CommentDto;
import ru.practicum.dto.CommentUpdateDto;
import ru.practicum.service.CommentService;

import javax.validation.Valid;

@Validated
@RestController
@RequestMapping("/users/{userId}/comments")
@Slf4j
@RequiredArgsConstructor
public class CommentPrivateController {

    private final CommentService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto add(@PathVariable long userId,
                          @RequestParam long eventId,
                          @Valid @RequestBody CommentCreateDto comment) {

        log.info("Добавление нового комментария {}", eventId);

        comment.setAuthorId(userId);
        comment.setEventId(eventId);
        CommentDto savedComment = service.add(comment);

        log.info("Комментарий добавлен");
        return savedComment;
    }

    @GetMapping
    public CommentDto get(@PathVariable long userId,
                          @RequestParam long eventId) {
        log.info("Получение своего комментария для конкретного события");

        CommentDto comments = service.get(userId, eventId);

        log.info("Комментарии получен");
        return comments;
    }

    @PatchMapping("/{commentId}")
    public CommentDto update(@PathVariable long userId,
                             @PathVariable long commentId,
                             @Valid @RequestBody CommentUpdateDto commentUpdateDto) {
        log.info("Редактирование комментария");
        commentUpdateDto.setAuthor(userId);
        commentUpdateDto.setId(commentId);
        CommentDto updatedComment = service.update(commentUpdateDto);
        log.info("Комментария отредактирована");
        return updatedComment;
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long userId,
                       @PathVariable long commentId) {
        log.info("Удаление комментария");
        service.delete(commentId);
        log.info("Комментарии удален");
    }
}


