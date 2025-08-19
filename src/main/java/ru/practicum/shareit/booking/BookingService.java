package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingSave;
import ru.practicum.shareit.booking.dto.BookingUpdate;
import ru.practicum.shareit.booking.dto.BookingViewDTO;

import java.util.List;

public interface BookingService {

    void deleteByUserId(long id);

    public BookingViewDTO findOne(Long id, Long userId);

    public Booking save(BookingSave bookingSave, long userId);

    public void update(BookingUpdate bookingUpdate, Long ownerId);

    public Booking bookingConfirmationOrRejection(long bookingId, Boolean approved, long userId);

    public List<BookingViewDTO> findAllByUserId(Long userId, BookingStatus status);

    public List<BookingViewDTO> findAllByOwnerId(Long ownerId, BookingStatus status);
}
