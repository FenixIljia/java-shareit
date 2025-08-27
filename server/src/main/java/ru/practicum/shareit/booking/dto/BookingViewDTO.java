package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class BookingViewDTO {

    @JsonProperty("id")
    private Long bookingId;

    private Item item;

    @JsonProperty("booker")
    private User user;

    @JsonProperty("start")
    private LocalDateTime startDate;

    @JsonProperty("end")
    private LocalDateTime endDate;

    @JsonProperty("status")
    private BookingStatus bookingStatus;
}
