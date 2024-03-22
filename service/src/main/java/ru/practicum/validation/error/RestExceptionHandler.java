package ru.practicum.validation.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.practicum.validation.exception.ConflictException;
import ru.practicum.validation.exception.EventValidationException;
import ru.practicum.validation.exception.NotFoundException;
import ru.practicum.validation.exception.ValidationException;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@RestControllerAdvice
@Slf4j
public class RestExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFoundException(final NotFoundException ex) {
        log.error("NotFoundException: {}", ex.getMessage());
        return new ApiError(
                "NOT_FOUND",
                "The required user was not found.",
                ex.getMessage(),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        );
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleNotFoundException(final ValidationException ex) {
        log.error("NotFoundException: {}", ex.getMessage());
        return new ApiError(
                "BAD_REQUEST",
                "Incorrectly made request.",
                ex.getMessage(),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ApiError handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return new ApiError(
                "BAD_REQUEST",
                "Incorrectly made request.",
                String.format("Failed to convert value of type java.lang.String to required type %s; nested exception is java.lang.NumberFormatException: For input string: %s",
                        Objects.requireNonNull(ex.getRequiredType()).getSimpleName(), ex.getValue()),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {

        String message = Objects.requireNonNull(ex.getBindingResult().getFieldError()).getDefaultMessage();

        return new ApiError(
                "BAD_REQUEST",
                "Incorrectly made request.",
                message,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        );
    }

    @ExceptionHandler(EventValidationException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiError handleForbiddenException(EventValidationException ex) {
        return new ApiError(
                "FORBIDDEN",
                "For the requested operation the conditions are not met.",
                ex.getMessage(),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleDataIntegrityViolationException(DataIntegrityViolationException ex) {

        String errorMessage = Objects.requireNonNull(ex.getMessage());

        return new ApiError(
                "CONFLICT",
                "Integrity constraint has been violated.",
                errorMessage,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        );
    }

    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConflictException(ConflictException ex) {
        return new ApiError(
                "CONFLICT",
                "For the requested operation the conditions are not met.",
                ex.getMessage(),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        );


    }

}



