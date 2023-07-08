package ru.practicum.shareit.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.bind.MissingRequestHeaderException;
import ru.practicum.shareit.booking.exception.BookingNotFoundException;
import ru.practicum.shareit.item.exception.IllegalCommentException;
import ru.practicum.shareit.item.exception.ItemNotAvailableException;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.exception.WrongOwnerException;
import ru.practicum.shareit.request.exception.RequestNotFoundException;
import ru.practicum.shareit.user.exception.EmailAlreadyExistsException;
import ru.practicum.shareit.user.exception.UserNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ErrorHandlerTest {
    private ErrorHandler errorHandler;

    @BeforeEach
    void setUp() {
        errorHandler = new ErrorHandler();
    }

    @Test
    void handleValidates() {
        MissingRequestHeaderException e = new MissingRequestHeaderException(
                "asd", null);
        String s = errorHandler.handleValidates(e);

        assertEquals("Пустой заголовок запроса - \"X-Sharer-User-Id\"", s);
    }

    @Test
    void handleBookingNotFoundException() {
        BookingNotFoundException e = new BookingNotFoundException("message");

        ErrorResponse result = errorHandler.handleBookingNotFoundException(e);

        assertEquals(e.getMessage(), result.getError());
    }

/*    @Test
    void handleBookingWrongStateException() {
        BookingWrongStateRequestedException e = new BookingWrongStateRequestedException("message");

        ErrorResponse result = errorHandler.handleBookingWrongStateException(e);

        assertEquals(e.getMessage(), result.getError());
    }*/

    @Test
    void handleEmailAlreadyExistException() {
        EmailAlreadyExistsException e = new EmailAlreadyExistsException("message");

        ErrorResponse result = errorHandler.handleEmailAlreadyExistException(e);

        assertEquals(e.getMessage(), result.getError());
    }

    @Test
    void handleUserNotFoundException() {
        UserNotFoundException e = new UserNotFoundException("message");

        ErrorResponse result = errorHandler.handleUserNotFoundException(e);

        assertEquals(e.getMessage(), result.getError());
    }

    @Test
    void handleIllegalCommentException() {
        IllegalCommentException e = new IllegalCommentException("message");

        ErrorResponse result = errorHandler.handleIllegalCommentException(e);

        assertEquals(e.getMessage(), result.getError());
    }

    @Test
    void handleItemNotAvailableException() {
        ItemNotAvailableException e = new ItemNotAvailableException("message");

        ErrorResponse result = errorHandler.handleItemNotAvailableException(e);

        assertEquals(e.getMessage(), result.getError());
    }

    @Test
    void handleItemNotFoundException() {
        ItemNotFoundException e = new ItemNotFoundException("message");

        ErrorResponse result = errorHandler.handleItemNotFoundException(e);

        assertEquals(e.getMessage(), result.getError());
    }

    @Test
    void handleWrongOwnerException() {
        WrongOwnerException e = new WrongOwnerException("message");

        ErrorResponse result = errorHandler.handleWrongOwnerException(e);

        assertEquals(e.getMessage(), result.getError());
    }

    @Test
    void handleRequestNotFoundException() {
        RequestNotFoundException e = new RequestNotFoundException("message");

        ErrorResponse result = errorHandler.handleRequestNotFoundException(e);

        assertEquals(e.getMessage(), result.getError());
    }
}