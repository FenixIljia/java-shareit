package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.dto.ItemView;
import ru.practicum.shareit.item.dto.ItemViewOwner;
import ru.practicum.shareit.request.dto.ResponseViewDto;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Integer> {
    @Query("SELECT new ru.practicum.shareit.item.dto.ItemViewOwner(" +
            "i.itemId, i.name, i.description, i.available, " +
            "lastBooking.bookingId, lastBooking.startDate, lastBooking.endDate, lastBooking.bookingStatus, " +
            "lastBooker.id, lastBooker.name, " +
            "nextBooking.bookingId, nextBooking.startDate, nextBooking.endDate, nextBooking.bookingStatus, " +
            "nextBooker.id, nextBooker.name) " +
            "FROM Item i " +
            "LEFT JOIN Booking lastBooking ON lastBooking.item.itemId = i.itemId " +
            "AND lastBooking.startDate = (" +
            "   SELECT MAX(b.startDate) FROM Booking b " +
            "   WHERE b.item.itemId = i.itemId " +
            "   AND b.startDate < CURRENT_TIMESTAMP " +
            "   AND b.bookingStatus = ru.practicum.shareit.booking.BookingStatus.APPROVED" +
            ") " +
            "LEFT JOIN User lastBooker ON lastBooker.id = lastBooking.user.id " +
            "LEFT JOIN Booking nextBooking ON nextBooking.item.itemId = i.itemId " +
            "AND nextBooking.startDate = (" +
            "   SELECT MIN(b.startDate) FROM Booking b " +
            "   WHERE b.item.itemId = i.itemId " +
            "   AND b.startDate > CURRENT_TIMESTAMP " +
            "   AND b.bookingStatus = ru.practicum.shareit.booking.BookingStatus.APPROVED" +
            ") " +
            "LEFT JOIN User nextBooker ON nextBooker.id = nextBooking.user.id " +
            "WHERE i.user.id = :userId")
    List<ItemViewOwner> findAllByUserId(@Param("userId") Long userId);

    @Query("SELECT i FROM Item i WHERE " +
            "(LOWER(i.name) LIKE LOWER(CONCAT('%', :text, '%')) OR " +
            "(LOWER(i.description) LIKE LOWER(CONCAT('%', :text, '%')))) " +
            "AND i.available = true")
    List<ItemView> findAllAvailableTrue(@Param("text") String text);

    @Query("SELECT new ru.practicum.shareit.item.dto.ItemViewOwner(i.itemId, i.user.id, i.name, i.description, i.available)" +
            " FROM Item i WHERE i.itemId = :itemId")
    Optional<ItemViewOwner> findByItemId(@Param("itemId") long itemId);

    @Query("SELECT new ru.practicum.shareit.request.dto.ResponseViewDto(" +
            "i.user.id, " +
            "i.itemId, " +
            "i.name" +
            ") " +
            "FROM Item i " +
            "WHERE i.request.id = :requestId")
    List<ResponseViewDto> findAllByRequestId(@Param("requestId") Long requestId);
}