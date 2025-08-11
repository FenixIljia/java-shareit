package ru.practicum.shareit.exception;

import jakarta.validation.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionResponse handleNotFound(NotFoundException e) {
        return new ExceptionResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse handlerValidationException(ValidationException e) {
        return new ExceptionResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ExceptionResponse handlerDuplicationDataException(DuplicatedDataException e) {
        return new ExceptionResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ExceptionResponse handlerException(AccessRightException e) {
        return new ExceptionResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse handlerException(ValidationDataException e) {
        return new ExceptionResponse(e.getMessage());
    }
}
