package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.booking.BookingStatus;

@Data
public class BookingUpdate {

    private long bookingId;

    private long ownerId;

    private BookingStatus status;
}
