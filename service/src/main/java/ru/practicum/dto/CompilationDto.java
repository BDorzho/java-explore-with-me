package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.validation.OnCreate;
import ru.practicum.validation.OnUpdate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompilationDto {

    private Long id;

    @NotBlank(message = "Название подборки не может быть пустым", groups = OnCreate.class)
    @Size(min = 1, max = 50, message = "Название подборки должно содержать от 1 до 50 символов.", groups = {OnCreate.class, OnUpdate.class})
    private String title;

    private Boolean pinned = false;

    private List<Long> events;
}
