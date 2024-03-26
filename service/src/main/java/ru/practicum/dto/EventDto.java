package ru.practicum.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.validation.OnCreate;
import ru.practicum.validation.validators.ValidEventDate;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventDto {

    private Long id;

    @NotBlank(message = "Заголовок не может быть пустым", groups = OnCreate.class)
    @Size(min = 3, max = 120, message = "Заголовок события должно содержать от 3 до 120 символов.", groups = OnCreate.class)
    private String title;

    @NotBlank(message = "Описание не может быть пустым", groups = OnCreate.class)
    @Size(min = 20, max = 2000, message = "Краткое описание должно содержать от 20 до 2000 символов.", groups = OnCreate.class)
    private String annotation;

    @NotNull(message = "Категория не может быть пустым", groups = OnCreate.class)
    private Long category;

    private long initiatorId;

    private Boolean paid = false;

    @ValidEventDate
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    @NotBlank(message = "Описание не может быть пустым", groups = OnCreate.class)
    @Size(min = 20, max = 7000, message = "Описание должно содержать от 3 до 50 символов.", groups = OnCreate.class)
    private String description;

    @Min(value = 0, message = "Лимит участников не должно быть отрицательным", groups = OnCreate.class)
    private int participantLimit = 0;

    @NotNull(message = "Широта и долгота места проведения не может быть пустым", groups = OnCreate.class)
    private Location location;

    private Boolean requestModeration = true;

}
