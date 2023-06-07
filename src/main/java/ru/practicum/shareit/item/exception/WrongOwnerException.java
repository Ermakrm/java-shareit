package ru.practicum.shareit.item.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WrongOwnerException extends RuntimeException {
    public WrongOwnerException(String message) {
        super(message);
        log.warn(message);
    }
}
