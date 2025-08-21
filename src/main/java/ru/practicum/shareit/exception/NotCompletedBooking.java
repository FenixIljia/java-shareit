package ru.practicum.shareit.exception;

public class NotCompletedBooking extends RuntimeException {
    public NotCompletedBooking(String message) {
        super(message);
    }
}
