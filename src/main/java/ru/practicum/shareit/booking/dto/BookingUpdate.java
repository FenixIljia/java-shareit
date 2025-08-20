package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.practicum.shareit.booking.BookingStatus;

@Data
public class BookingUpdate {

    @NotNull
    private long bookingId;

    private long ownerId;

    private BookingStatus status;
}
