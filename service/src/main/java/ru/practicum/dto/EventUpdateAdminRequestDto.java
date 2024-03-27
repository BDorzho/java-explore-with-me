package ru.practicum.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.model.StateAction;
import ru.practicum.validation.OnUpdate;

import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventUpdateAdminRequestDto {

    private Long id;

    @Size(min = 20, max = 2000, message = "Краткое описание должно содержать от 20 до 2000 символов.", groups = OnUpdate.class)
    private String annotation;

    private Long category;

    @Size(min = 20, max = 7000, message = "Описание должно содержать от 3 до 50 символов.", groups = OnUpdate.class)
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    private Location location;

    private Boolean paid;

    @Min(value = 0, message = "Лимит участников не должно быть отрицательным", groups = OnUpdate.class)
    private int participantLimit;

    private Boolean requestModeration;

    private StateAction stateAction;

    @Size(min = 3, max = 120, message = "Заголовок события должно содержать от 3 до 120 символов.", groups = OnUpdate.class)
    private String title;
}
