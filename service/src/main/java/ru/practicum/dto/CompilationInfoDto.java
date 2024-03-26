package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompilationInfoDto {

    private Long id;
    private String title;
    private Boolean pinned;
    private List<EventShortDto> events;
}
