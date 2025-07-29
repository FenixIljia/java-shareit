package ru.practicum.shareit.exception;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ExceptionResponse {
    private final String message;

    public ExceptionResponse(String message) {
        this.message = message;
    }
}
