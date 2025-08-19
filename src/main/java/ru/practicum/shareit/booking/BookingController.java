package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingSave;
import ru.practicum.shareit.booking.dto.BookingViewDTO;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@AllArgsConstructor
public class BookingController {
    private BookingService bookingService;

    @PostMapping
    public Booking create(
            @RequestBody BookingSave bookingSave,
            @RequestHeader("X-Sharer-User-Id") long userId
    ) {
        return bookingService.save(bookingSave, userId);
    }

    @PatchMapping("/{bookingId}")
    public Booking bookingConfirmationOrRejection(
            @PathVariable Long bookingId,
            @RequestParam Boolean approved,
            @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        return bookingService.bookingConfirmationOrRejection(bookingId, approved, userId);
    }

    @GetMapping("/{bookingId}")
    public BookingViewDTO findByBookingId(
            @PathVariable Long bookingId,
            @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        return bookingService.findOne(bookingId, userId);
    }

    @GetMapping
    public List<BookingViewDTO> findAllByUserId(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(required = false) BookingStatus status
    ) {
        return bookingService.findAllByUserId(userId, status);
    }

    @GetMapping("/owner")
    public List<BookingViewDTO> findAllByOwnerId(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(required = false) BookingStatus status
    ) {
        return bookingService.findAllByOwnerId(userId, status);
    }
}
