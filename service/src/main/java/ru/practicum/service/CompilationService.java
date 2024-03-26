package ru.practicum.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.dto.CompilationDto;
import ru.practicum.dto.CompilationInfoDto;

import java.util.List;

public interface CompilationService {

    CompilationInfoDto add(CompilationDto compilationDto);

    void delete(Long compId);

    CompilationInfoDto update(CompilationDto compilationDto);

    List<CompilationInfoDto> get(Boolean pinned, Pageable pageable);

    CompilationInfoDto getById(Long compId);
}
