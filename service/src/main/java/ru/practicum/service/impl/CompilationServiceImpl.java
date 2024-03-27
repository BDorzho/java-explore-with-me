package ru.practicum.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dao.CompilationRepository;
import ru.practicum.dao.EventRepository;
import ru.practicum.dao.EventRequestRepository;
import ru.practicum.dto.CompilationDto;
import ru.practicum.dto.CompilationInfoDto;
import ru.practicum.dto.EventConfirmedRequestsInfo;
import ru.practicum.dto.EventShortDto;
import ru.practicum.mapper.CompilationMapper;
import ru.practicum.mapper.EventMapper;
import ru.practicum.model.Compilation;
import ru.practicum.model.Event;
import ru.practicum.service.CompilationService;
import ru.practicum.validation.exception.NotFoundException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository repository;

    private final EventRepository eventRepository;

    private final EventRequestRepository eventRequestRepository;

    private final CompilationMapper mapper;

    private final EventMapper eventMapper;

    @Override
    @Transactional
    public CompilationInfoDto add(CompilationDto compilationDto) {

        List<Event> events = eventRepository.findAllByIds(compilationDto.getEvents());

        Compilation savedCompilation = repository.save(mapper.toModel(compilationDto, events));

        Map<Long, Long> confirmedRequestsMap = getConfirmedRequestsByEventIds(compilationDto.getEvents());

        List<EventShortDto> eventDto = events.stream()
                .map(event -> {
                    Long confirmedRequests = confirmedRequestsMap.getOrDefault(event.getId(), 0L);
                    return eventMapper.toShortDto(event, null, confirmedRequests);
                })
                .collect(Collectors.toList());

        return mapper.toDto(savedCompilation, eventDto);
    }

    @Override
    @Transactional
    public void delete(long compId) {
        repository.deleteById(compId);
    }

    @Override
    @Transactional
    public CompilationInfoDto update(CompilationDto compilationDto) {
        Long compId = compilationDto.getId();
        Compilation compilation = repository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Compilation with id=" + compId + " was not found"));

        if (compilationDto.getTitle() != null) {
            compilation.setTitle(compilationDto.getTitle());
        }

        if (compilationDto.getPinned() != null) {
            compilation.setPinned(compilationDto.getPinned());
        }

        if (compilationDto.getEvents() != null && !compilationDto.getEvents().isEmpty()) {
            List<Event> events = eventRepository.findAllByIds(compilationDto.getEvents());
            compilation.setEvents(events);
        }

        Compilation savedCompilation = repository.save(compilation);

        Map<Long, Long> confirmedRequestsMap = getConfirmedRequestsByEventIds(compilationDto.getEvents());

        List<EventShortDto> eventDto = savedCompilation.getEvents().stream()
                .map(event -> {
                    Long confirmedRequests = confirmedRequestsMap.getOrDefault(event.getId(), 0L);
                    return eventMapper.toShortDto(event, null, confirmedRequests);
                })
                .collect(Collectors.toList());

        return mapper.toDto(savedCompilation, eventDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompilationInfoDto> get(Boolean pinned, Pageable pageable) {
        List<Compilation> compilations;

        if (pinned != null) {
            compilations = repository.findByPinned(pinned, pageable);
        } else {
            compilations = repository.findAll(pageable).getContent();
        }

        List<Long> eventIds = compilations.stream()
                .flatMap(compilation -> compilation.getEvents().stream().map(Event::getId))
                .collect(Collectors.toList());

        Map<Long, Long> confirmedRequestsMap = getConfirmedRequestsByEventIds(eventIds);

        return compilations.stream()
                .map(compilation -> {
                    List<EventShortDto> eventDto = compilation.getEvents().stream()
                            .map(event -> {
                                Long confirmedRequests = confirmedRequestsMap.getOrDefault(event.getId(), 0L);
                                return eventMapper.toShortDto(event, null, confirmedRequests);
                            })
                            .collect(Collectors.toList());

                    return mapper.toDto(compilation, eventDto);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CompilationInfoDto getById(long compId) {
        Compilation compilation = repository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Compilation with id=" + compId + " was not found"));

        List<Long> eventIds = compilation.getEvents().stream()
                .map(Event::getId)
                .collect(Collectors.toList());

        Map<Long, Long> confirmedRequestsMap = getConfirmedRequestsByEventIds(eventIds);

        List<EventShortDto> eventDto = compilation.getEvents().stream()
                .map(event -> {
                    Long confirmedRequests = confirmedRequestsMap.getOrDefault(event.getId(), 0L);
                    return eventMapper.toShortDto(event, null, confirmedRequests);
                })
                .collect(Collectors.toList());

        return mapper.toDto(compilation, eventDto);
    }

    private Map<Long, Long> getConfirmedRequestsByEventIds(List<Long> eventIds) {
        List<EventConfirmedRequestsInfo> results = eventRequestRepository.countConfirmedRequestsByEventIds(eventIds);

        Map<Long, Long> confirmedRequestsMap = new HashMap<>();
        for (EventConfirmedRequestsInfo result : results) {
            confirmedRequestsMap.put(result.getEventId(), result.getConfirmedRequests());
        }

        return confirmedRequestsMap;
    }


}

