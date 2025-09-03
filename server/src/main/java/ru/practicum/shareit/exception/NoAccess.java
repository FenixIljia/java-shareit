package ru.practicum.shareit.exception;

public class NoAccess extends RuntimeException {
    public NoAccess(String message) {
        super(message);
    }
}
