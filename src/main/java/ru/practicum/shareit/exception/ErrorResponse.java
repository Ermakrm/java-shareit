package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ValidationException;
import java.util.Map;

@RestControllerAdvice
class ErrorResponse {
    @ExceptionHandler({IllegalArgumentException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleWrongArgument(final RuntimeException e) {
        return Map.of("Wrong argument", e.getMessage());
    }

    @ExceptionHandler({ValidationException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleValidate(final RuntimeException e) {
        return Map.of("Validation error", e.getMessage());
    }
}