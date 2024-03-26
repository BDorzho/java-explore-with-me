package ru.practicum.stats;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatsCreateDto {

    int id;

    @NotNull(message = "Идентификатор сервиса не может быть пустым")
    private String app;

    @NotNull(message = "URI для которого был осуществлен запрос не может быть пустым")
    private String uri;

    @NotNull(message = "IP-адрес пользователя, осуществившего запрос не может быть пустым")
    private String ip;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timeStamp;


    public StatsCreateDto(String app, String uri, String ip, LocalDateTime timeStamp) {
        this.app = app;
        this.uri = uri;
        this.ip = ip;
        this.timeStamp = timeStamp;
    }
}
