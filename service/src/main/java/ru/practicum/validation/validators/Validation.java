package ru.practicum.validation.validators;

import org.springframework.stereotype.Component;
import ru.practicum.validation.exception.ValidationException;

import java.time.LocalDateTime;

@Component
public class Validation {

    public void date(LocalDateTime createDate) {
        if (createDate != null) {
            if (createDate.isBefore(LocalDateTime.now().plusHours(2))) {
                throw new ValidationException("Field: eventDate. Error: должно содержать дату, которая еще не наступила. Value: " + createDate);
            }
        }
    }
}
