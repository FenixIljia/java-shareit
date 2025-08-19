package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingSave;
import ru.practicum.shareit.booking.dto.BookingUpdate;
import ru.practicum.shareit.booking.dto.BookingViewDTO;
import ru.practicum.shareit.exception.AccessRightException;
import ru.practicum.shareit.exception.NoAccess;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {
    private BookingRepository bookingRepository;
    private UserRepository userRepository;
    private ItemRepository itemRepository;

    @Override
    public void deleteByUserId(long id) {
        userRepository.findById(id);
        bookingRepository.deleteByUserId(id);
    }

    @Transactional(readOnly = true)
    @Override
    public BookingViewDTO findOne(Long bookingId, Long userId) {
        userRepository.findById(userId);
        Optional<BookingViewDTO> booking = bookingRepository.findOneByBookingId(bookingId);
        if (booking.isPresent()) {
            if (!(booking.get().getItem().getUser().getId().equals(userId) || booking.get().getUser().getId().equals(userId))) {
                throw new AccessRightException("Access Denied");
            }
            return booking.get();
        } else {
            throw new NotFoundException("Booking with id " + bookingId + " not found");
        }
    }

    @Override
    public Booking save(BookingSave bookingSave, long userId) {
        Optional<Item> item = itemRepository.findById((int) bookingSave.getItemId());
        if (item.isEmpty()) {
            throw new NotFoundException("Item with id " + bookingSave.getItemId() + " not found");
        }
        if (!item.get().getAvailable()) {
            throw new NoAccess("Item with id " + bookingSave.getItemId() + " not available");
        }
        Booking booking = Booking.builder()
                .user(userRepository.findById(userId).get())
                .item(item.get())
                .startDate(bookingSave.getStart())
                .endDate(bookingSave.getEnd())
                .bookingStatus(BookingStatus.WAITING)
                .build();
        return bookingRepository.save(booking);
    }

    @Override
    public void update(BookingUpdate bookingUpdate, Long ownerId) {
        userRepository.findById(ownerId);
        Optional<Booking> booking = bookingRepository.findById((int) bookingUpdate.getBookingId());
        if (booking.isEmpty()) {
            throw new NotFoundException("Booking with id " + bookingUpdate.getBookingId() + " not found");
        }
        if (!booking.get().getUser().getId().equals(ownerId)) {
            throw new AccessRightException("User with id " + ownerId + " is not the owner of item with id " + booking.get().getItem().getItemId());
        }
        booking.get().setBookingStatus(bookingUpdate.getStatus());
        bookingRepository.save(booking.get());
    }

    @Override
    public Booking bookingConfirmationOrRejection(long bookingId, Boolean approved, long userId) {
        userRepository.findById(userId);
        Optional<Booking> booking = bookingRepository.findById((int) bookingId);
        if (booking.isEmpty()) {
            throw new NotFoundException("Booking with id " + bookingId + " not found");
        }
        if (!booking.get().getItem().getUser().getId().equals(userId)) {
            throw new AccessRightException("Access Denied");
        }
        if (approved) {
            booking.get().setBookingStatus(BookingStatus.APPROVED);
        } else {
            booking.get().setBookingStatus(BookingStatus.REJECTED);
        }
        return bookingRepository.save(booking.get());
    }

    @Override
    public List<BookingViewDTO> findAllByUserId(Long userId, BookingStatus status) {
        userRepository.findById(userId);
        if (status != null) {
            return bookingRepository.findAllByUserIdAndBookingStatusOrderByStartDateAsc(userId, status);
        } else {
            return bookingRepository.findAllByUserIdOrderByStartDateAsc(userId);
        }
    }

    @Override
    public List<BookingViewDTO> findAllByOwnerId(Long ownerId, BookingStatus status) {
        userRepository.findById(ownerId);
        List<BookingViewDTO> bookingOwner = new ArrayList<>();
        if (status != null) {
            bookingOwner = bookingRepository.findAllByOwnerIdAndBookingStatus(ownerId, status);
        } else {
            bookingOwner = bookingRepository.findAllByOwnerId(ownerId);
        }
        if (bookingOwner.isEmpty()) {
            throw new NotFoundException("Booking with ownerId " + ownerId + " not found");
        }
        return bookingOwner;
    }
}
