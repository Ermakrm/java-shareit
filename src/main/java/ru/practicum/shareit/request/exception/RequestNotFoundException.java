package ru.practicum.shareit.request.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RequestNotFoundException extends RuntimeException {
    public RequestNotFoundException(String message) {
        super(message);
        log.warn(message);
    }
}
