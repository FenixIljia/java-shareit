package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.dto.BookingViewDTO;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

    @Query("SELECT new ru.practicum.shareit.booking.dto.BookingViewDTO(" +
            "b.bookingId, " +
            "b.item, " +
            "b.user, " +
            "b.startDate, " +
            "b.endDate, " +
            "b.bookingStatus) " +
            "FROM Booking b WHERE b.bookingId = :bookingId")
    Optional<BookingViewDTO> findOneByBookingId(Long bookingId);

    @Query("SELECT new ru.practicum.shareit.booking.dto.BookingViewDTO(" +
            "b.bookingId, " +
            "b.item, " +
            "b.user, " +
            "b.startDate, " +
            "b.endDate, " +
            "b.bookingStatus) " +
            "FROM Booking b WHERE b.item.itemId = :itemId")
    List<BookingViewDTO> findAllByItem_ItemId(Long itemId);

    void deleteByUserId(long userId);

    List<BookingViewDTO> findAllByUserIdAndBookingStatusOrderByStartDateAsc(Long userId, BookingStatus bookingStatus);

    @Query("SELECT new ru.practicum.shareit.booking.dto.BookingViewDTO(" +
            "b.bookingId," +
            " b.item," +
            " b.user," +
            " b.startDate," +
            " b.endDate," +
            " b.bookingStatus) " +
            "FROM Booking b WHERE b.user.id = :userId ORDER BY b.startDate ASC")
    List<BookingViewDTO> findAllByUserIdOrderByStartDateAsc(@Param("userId") Long userId);

    @Query("SELECT new ru.practicum.shareit.booking.dto.BookingViewDTO(" +
            "b.bookingId, " +
            "b.item, " +
            "b.user, " +
            "b.startDate, " +
            "b.endDate, " +
            "b.bookingStatus) " +
            "FROM Booking b WHERE b.item.user.id = :userId")
    List<BookingViewDTO> findAllByOwnerId(Long userId);

    @Query("SELECT new ru.practicum.shareit.booking.dto.BookingViewDTO(" +
            "b.bookingId, " +
            "b.item, " +
            "b.user, " +
            "b.startDate, " +
            "b.endDate, " +
            "b.bookingStatus) " +
            "FROM Booking b WHERE (b.item.user.id = :ownerId OR b.bookingStatus = :bookingStatus)")
    List<BookingViewDTO> findAllByOwnerIdAndBookingStatus(@Param("ownerId") Long ownerId, @Param("bookingStatus") BookingStatus bookingStatus);

    @Query("SELECT new ru.practicum.shareit.booking.dto.BookingViewDTO(" +
            " b.bookingId," +
            " b.item," +
            " b.user," +
            " b.startDate," +
            " b.endDate," +
            " b.bookingStatus" +
            ")" +
            "FROM Booking b WHERE b.item.itemId = :itemId AND b.user.id = :userId")
    List<BookingViewDTO> findAllByUserIdAndItemItemId(@Param("userId") Long userId, @Param("itemId") Long itemId);
}