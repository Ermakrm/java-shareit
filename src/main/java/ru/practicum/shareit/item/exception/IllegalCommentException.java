package ru.practicum.shareit.item.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class IllegalCommentException extends RuntimeException {
    public IllegalCommentException(String message) {
        super(message);
        log.warn(message);
    }
}
