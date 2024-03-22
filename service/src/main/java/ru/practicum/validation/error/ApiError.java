package ru.practicum.validation.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApiError {
    private String status;
    private String reason;
    private String message;
    private String timestamp;
}
