package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.model.RequestStatus;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventUpdateRequestStatusDto {

    @NotEmpty(message = "Список идентификаторов запросов не может быть пустым")
    private List<Long> requestIds;

    @NotNull(message = "Статус запроса не может быть пустым")
    private RequestStatus status;

}
