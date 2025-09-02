package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingSave;
import ru.practicum.shareit.booking.dto.BookingViewDTO;
import ru.practicum.shareit.constant.HttpHeaders;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@AllArgsConstructor
public class BookingController {

    private BookingService bookingService;

    @PostMapping
    public Booking create(
            @RequestBody BookingSave bookingSave,
            @RequestHeader(HttpHeaders.USER_ID_HEADER) long userId
    ) {
        return bookingService.save(bookingSave, userId);
    }

    @PatchMapping("/{bookingId}")
    public Booking bookingConfirmationOrRejection(
            @PathVariable long bookingId,
            @RequestParam Boolean approved,
            @RequestHeader(HttpHeaders.USER_ID_HEADER) long userId
    ) {
        return bookingService.bookingConfirmationOrRejection(bookingId, approved, userId);
    }

    @GetMapping("/{bookingId}")
    public BookingViewDTO findByBookingId(
            @PathVariable Long bookingId,
            @RequestHeader(HttpHeaders.USER_ID_HEADER) Long userId
    ) {
        return bookingService.findOne(bookingId, userId);
    }

    @GetMapping
    public List<BookingViewDTO> findAllByUserId(
            @RequestHeader(HttpHeaders.USER_ID_HEADER) Long userId,
            @RequestParam(required = false) BookingStatus status
    ) {
        return bookingService.findAllByUserId(userId, status);
    }

    @GetMapping("/owner")
    public List<BookingViewDTO> findAllByOwnerId(
            @RequestHeader(HttpHeaders.USER_ID_HEADER) Long userId,
            @RequestParam(required = false) BookingStatus status
    ) {
        return bookingService.findAllByOwnerId(userId, status);
    }
}
