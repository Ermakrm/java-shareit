package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.booking.exception.BookingNotFoundException;
import ru.practicum.shareit.booking.exception.BookingWrongStateRequestedException;

import javax.validation.ValidationException;
import java.util.Map;
import java.util.Objects;

@RestControllerAdvice
class ErrorResponse {
    @ExceptionHandler({IllegalArgumentException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleWrongArgument(final RuntimeException e) {
        return Map.of("Wrong argument", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleValidate(final ValidationException e) {
        return Map.of("Validation error", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleValidates(final MissingRequestHeaderException ignore) {
        return "Пустой заголовок запроса - \"X-Sharer-User-Id\"";
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleBookingNotFoundException(final BookingNotFoundException e) {
        return Map.of("error", Objects.requireNonNull(e.getMessage()));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleBookingWrongStateException(final BookingWrongStateRequestedException e) {
        return Map.of("error", Objects.requireNonNull(e.getMessage()));
    }
}
