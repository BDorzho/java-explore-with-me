package ru.practicum.controller.pub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.CompilationInfoDto;
import ru.practicum.service.CompilationService;

import java.util.List;

@RestController
@RequestMapping("/compilations")
@Slf4j
@RequiredArgsConstructor
public class CompilationPublicController {


    private final CompilationService compilationService;

    @GetMapping
    public List<CompilationInfoDto> get(@RequestParam(name = "pinned", required = false) Boolean pinned,
                                        @RequestParam(defaultValue = "0") Integer from,
                                        @RequestParam(defaultValue = "10") Integer size) {

        log.info("Получение подборок событий");
        Pageable pageable = PageRequest.of(from / size, size);
        List<CompilationInfoDto> compilations = compilationService.get(pinned, pageable);
        log.info("Найдены подборки событий");

        return compilations;
    }

    @GetMapping("/{compId}")
    public CompilationInfoDto getById(@PathVariable Long compId) {

        log.info("Получение подборки событий по его id - {}", compId);
        CompilationInfoDto compilation = compilationService.getById(compId);
        log.info("Подборка событий найдена");

        return compilation;

    }
}

