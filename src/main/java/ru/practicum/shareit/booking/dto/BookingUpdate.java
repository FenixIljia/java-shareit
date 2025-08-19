package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.practicum.shareit.booking.BookingStatus;

import java.time.LocalDate;

@Data
public class BookingUpdate {

    @NotNull
    private long bookingId;

    private long ownerId;

    private BookingStatus status;
}
