package ru.practicum.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;
import ru.practicum.model.StateAction;
import ru.practicum.validation.OnUpdate;
import ru.practicum.validation.validators.ValidEventDate;

import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class UpdateEventUserDto {

    @Size(min = 20, max = 2000, message = "Аннотация должна содержать от 20 до 2000 символов", groups = OnUpdate.class)
    @Nullable
    private String annotation;

    @Nullable
    private Long category;

    @Size(min = 20, max = 7000, message = "Описание должно содержать от 20 до 7000 символов.", groups = OnUpdate.class)
    @Nullable
    private String description;

    @ValidEventDate
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Nullable
    private LocalDateTime eventDate;

    @Nullable
    private Location location;

    private Boolean paid;

    @Min(value = 0, message = "Лимит участников не должно быть отрицательным", groups = OnUpdate.class)
    private int participantLimit = 0;

    private Boolean requestModeration;

    @Nullable
    private StateAction stateAction;

    @Size(min = 3, max = 120, message = "Заголовок должен содержать от 3 до 120 символов", groups = OnUpdate.class)
    @Nullable
    private String title;
}
