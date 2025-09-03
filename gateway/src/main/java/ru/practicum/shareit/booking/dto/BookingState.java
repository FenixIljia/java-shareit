package ru.practicum.shareit.booking.dto;

import java.util.Optional;

/**
 * Перечисление статусов бронирования для фильтрации запросов.
 * Используется для указания состояния бронирований при запросе списка.
 */
public enum BookingState {
	/**
	 * Все бронирования без применения фильтров
	 */
	ALL,

	/**
	 * Текущие активные бронирования (начались, но еще не закончились)
	 */
	CURRENT,

	/**
	 * Будущие бронирования (дата начала еще не наступила)
	 */
	FUTURE,

	/**
	 * Завершенные бронирования (дата окончания уже прошла)
	 */
	PAST,

	/**
	 * Отклоненные бронирования (статус REJECTED)
	 */
	REJECTED,

	/**
	 * Бронирования, ожидающие подтверждения (статус WAITING)
	 */
	WAITING;

	/**
	 * Конвертирует строковое представление в соответствующий enum BookingState.
	 * Сравнение происходит без учета регистра.
	 *
	 * @param stringState строковое представление статуса
	 * @return Optional с найденным BookingState, или empty если соответствие не найдено
	 * @throws IllegalArgumentException если передан null
	 */
	public static Optional<BookingState> from(String stringState) {
		if (stringState == null) {
			throw new IllegalArgumentException("State cannot be null");
		}

		for (BookingState state : values()) {
			if (state.name().equalsIgnoreCase(stringState)) {
				return Optional.of(state);
			}
		}
		return Optional.empty();
	}
}