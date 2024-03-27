package ru.practicum.controller.adm;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.CompilationDto;
import ru.practicum.dto.CompilationInfoDto;
import ru.practicum.service.CompilationService;
import ru.practicum.validation.OnCreate;
import ru.practicum.validation.OnUpdate;

@RestController
@Validated
@RequestMapping("/admin/compilations")
@Slf4j
@AllArgsConstructor
public class CompilationAdminController {

    private final CompilationService compilationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationInfoDto add(@Validated(OnCreate.class)
                                  @RequestBody CompilationDto compilationDto) {
        log.info("Добавление новой подборки");
        CompilationInfoDto createCompilation = compilationService.add(compilationDto);
        log.info("Подборка добавлена");
        return createCompilation;
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long compId) {
        log.info("Удаление подборки");
        compilationService.delete(compId);
        log.info("Подборка удалена");
    }

    @PatchMapping("/{compId}")
    public CompilationInfoDto update(@PathVariable long compId,
                                     @Validated(OnUpdate.class) @RequestBody CompilationDto compilationDto) {
        log.info("Обновление подборки");
        compilationDto.setId(compId);
        CompilationInfoDto updateCompilation = compilationService.update(compilationDto);
        log.info("Подборка обновлена");
        return updateCompilation;
    }
}