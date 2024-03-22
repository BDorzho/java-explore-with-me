package ru.practicum.validation.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class EventDateValidator implements ConstraintValidator<ValidEventDate, LocalDateTime> {

    @Override
    public boolean isValid(LocalDateTime eventDate, ConstraintValidatorContext context) {

        LocalDateTime currentDate = LocalDateTime.now();

        return eventDate.isAfter(currentDate);
    }
}
