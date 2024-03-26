package ru.practicum.validation.validators;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EventDateValidator.class)
@Documented
public @interface ValidEventDate {
    String message() default "Дата и время на которое намечено событие не может быть пустым или в прошлом";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
