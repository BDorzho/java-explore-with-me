package ru.practicum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class SaveCommonDto {

    int id;

    @NotNull(message = "Идентификатор сервиса не может быть пустым")
    private String app;

    @NotNull(message = "URI для которого был осуществлен запрос не может быть пустым")
    private String uri;

    @NotNull(message = "IP-адрес пользователя, осуществившего запрос не может быть пустым")
    private String ip;

    private LocalDateTime timeStamp;

}
