package ru.practicum.validation.error;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@NoArgsConstructor
public class ApiError {
    private String status;
    private String reason;
    private String message;
    private String timestamp;
    private List<String> errors;

    public ApiError(String status, String reason, String message, String timestamp) {
        this.status = status;
        this.reason = reason;
        this.message = message;
        this.timestamp = timestamp;
    }

    public ApiError(String status, String reason, String message, String timestamp, List<String> errors) {
        this.status = status;
        this.reason = reason;
        this.message = message;
        this.timestamp = timestamp;
        this.errors = errors;
    }
}
