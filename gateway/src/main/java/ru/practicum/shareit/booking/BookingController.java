package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;


@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @GetMapping
    public ResponseEntity<Object> findAllByUserId(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(name = "state", defaultValue = "all") String stateParam,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size
    ) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        return bookingClient.getBookings(userId, state, from, size);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> findByBookingId(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable Long bookingId
    ) {
        log.info("Get booking {}, userId={}", bookingId, userId);
        return bookingClient.findByBookingId(userId, bookingId);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> findByOwnerItemId(
            @RequestHeader("X-Sharer-User-Id") long ownerItemId,
            @RequestParam(name = "state", defaultValue = "all") String stateParam
    ) {
        BookingState bookingState = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("Get booking with state {}", stateParam);
        return bookingClient.findByOwnerItemId(ownerItemId, bookingState);
    }

    @PostMapping
    public ResponseEntity<Object> create(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestBody @Valid BookItemRequestDto requestDto
    ) {
        log.info("Creating booking {}, userId={}", requestDto, userId);
        return bookingClient.create(userId, requestDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> bookingConfirmationOrRejection(
            @PathVariable long bookingId,
            @RequestParam Boolean approved,
            @RequestHeader("X-Sharer-User-Id") long userId
    ) {
        return bookingClient.bookingConfirmationOrRejection(bookingId, approved, userId);
    }
}
