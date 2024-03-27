package ru.practicum.controller.pub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.CommentDto;
import ru.practicum.service.CommentService;

import java.util.List;

@RestController
@RequestMapping("/events/{eventId}")
@Slf4j
@RequiredArgsConstructor
public class CommentPublicController {

    private final CommentService service;

    @GetMapping("/comments")
    public List<CommentDto> getById(@PathVariable long eventId,
                                    @RequestParam(defaultValue = "0") int from,
                                    @RequestParam(defaultValue = "10") int size) {

        log.info("Получение комментариев для конкретного события");
        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "created"));
        List<CommentDto> commentList = service.getBy(eventId, pageable);

        log.info("Событие найдено");

        return commentList;

    }
}
