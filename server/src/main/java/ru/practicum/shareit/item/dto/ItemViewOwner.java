package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingViewDTO;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class ItemViewOwner {
    @JsonProperty("id")
    private Long itemId;
    private Long ownerId;
    private String name;
    private String description;
    private Boolean available;
    private BookingViewDTO lastBooking;
    private BookingViewDTO nextBooking;
    private List<CommentView> comments = new ArrayList<>();


    public ItemViewOwner(Long itemId, Long ownerId, String name, String description, Boolean available) {
        this.itemId = itemId;
        this.ownerId = ownerId;
        this.name = name;
        this.description = description;
        this.available = available;
        this.lastBooking = null;
        this.nextBooking = null;
    }

    public ItemViewOwner(
            Long itemId,
            Long ownerId,
            String name,
            String description,
            Boolean available,
            Long lastBookingId,
            LocalDateTime lastBookingStart,
            LocalDateTime lastBookingEnd,
            BookingStatus lastBookingStatus,
            Long lastBookerId,
            String lastBookerName,
            Long nextBookingId,
            LocalDateTime nextBookingStart,
            LocalDateTime nextBookingEnd,
            BookingStatus nextBookingStatus,
            Long nextBookerId,
            String nextBookerName
    ) {
        this.itemId = itemId;
        this.ownerId = ownerId;
        this.name = name;
        this.description = description;
        this.available = available;
        if (lastBookingId != null) {
            this.lastBooking = new BookingViewDTO();
            this.lastBooking.setBookingId(lastBookingId);
            this.lastBooking.setStartDate(lastBookingStart);
            this.lastBooking.setEndDate(lastBookingEnd);
            this.lastBooking.setBookingStatus(lastBookingStatus);
            User lastBooker = new User();
            lastBooker.setId(lastBookerId);
            lastBooker.setName(lastBookerName);
            this.lastBooking.setUser(lastBooker);
        }
        if (nextBookingId != null) {
            this.nextBooking = new BookingViewDTO();
            this.nextBooking.setBookingId(nextBookingId);
            this.nextBooking.setStartDate(nextBookingStart);
            this.nextBooking.setEndDate(nextBookingEnd);
            this.nextBooking.setBookingStatus(nextBookingStatus);

            User nextBooker = new User();
            nextBooker.setId(nextBookerId);
            nextBooker.setName(nextBookerName);
            this.nextBooking.setUser(nextBooker);
        }
    }
}
