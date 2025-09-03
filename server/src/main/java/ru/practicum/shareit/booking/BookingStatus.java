package ru.practicum.shareit.booking;

import java.util.Optional;

/**
 * Статусы бронирований для фильтрации
 */
public enum BookingStatus {
    /**
     * Одобренные бронирования
     */
    APPROVED,

    /**
     * Завершенные бронирования
     */
    PAST,

    /**
     * Будущие бронирования
     */
    FUTURE,

    /**
     * Бронирования, ожидающие подтверждения
     */
    WAITING,

    /**
     * Отклоненные бронирования
     */
    REJECTED;


    /**
     * Конвертирует строку в соответствующий BookingState
     *
     * @param state строковое представление статуса
     * @return Optional с найденным BookingState, или empty если не найден
     */
    public static Optional<BookingStatus> from(String state) {
        for (BookingStatus value : values()) {
            if (value.name().equalsIgnoreCase(state)) {
                return Optional.of(value);
            }
        }
        return Optional.empty();
    }
}