package ru.practicum.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.dto.CompilationDto;
import ru.practicum.dto.EventShortDto;
import ru.practicum.dto.CompilationInfoDto;
import ru.practicum.model.Compilation;
import ru.practicum.model.Event;

import java.util.List;

@Component
public class CompilationMapper {


    public Compilation toModel(CompilationDto dto, List<Event> events) {
        return new Compilation(dto.getId(),
                dto.getTitle(),
                events,
                dto.getPinned());
    }

    public CompilationInfoDto toDto(Compilation model, List<EventShortDto> eventShortDto) {
        return new CompilationInfoDto(model.getId(),
                model.getTitle(),
                model.getPinned(),
                eventShortDto
        );
    }
}
