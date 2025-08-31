package ru.practicum.shareit.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemViewOwner;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class ItemTest {

    @Test
    public void ItemConstructorTest() {
        User user = new User();
        user.setEmail("test");
        user.setName("test");
        user.setId(1L);

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setUserId(user);
        itemRequest.setDescription("test");
        itemRequest.setCreateDate(LocalDateTime.now());
        itemRequest.setId(1L);
        Item item = new Item(1L, user, "Test", "Test", true, itemRequest);
        Item item2 = new Item(1L, user, "Test", "Test", true, itemRequest);
        assertThat(item.hashCode(), equalTo(item2.hashCode()));
        assertThat(item.equals(item2), equalTo(true));
    }

    @Test
    public void ItemViewOwnerConstructorTest() {
        Long itemId = 1L;
        Long ownerId = 1L;
        String name = "Test";
        String description = "Test";
        Boolean available = true;
        Long lastBookingId = 1L;
        LocalDateTime lastBookingStart = LocalDateTime.now().minusDays(2);
        LocalDateTime lastBookingEnd = LocalDateTime.now().minusDays(1);
        BookingStatus lastBookingStatus = BookingStatus.APPROVED;
        Long lastBookerId = 1L;
        String lastBookerName = "Test";
        Long nextBookingId = 1L;
        LocalDateTime nextBookingStart = LocalDateTime.now().plusDays(2);
        LocalDateTime nextBookingEnd = LocalDateTime.now().plusDays(3);
        BookingStatus nextBookingStatus = BookingStatus.APPROVED;
        Long nextBookerId = 1L;
        String nextBookerName = "Test";
        ItemViewOwner itemViewOwner = new ItemViewOwner(
                itemId,
                ownerId,
                name,
                description,
                available,
                lastBookingId,
                lastBookingStart,
                lastBookingEnd,
                lastBookingStatus,
                lastBookerId,
                lastBookerName,
                nextBookingId,
                nextBookingStart,
                nextBookingEnd,
                nextBookingStatus,
                nextBookerId,
                nextBookerName
        );
        assertThat(itemViewOwner.getItemId(), equalTo(itemId));
        assertThat(itemViewOwner.getOwnerId(), equalTo(ownerId));
        assertThat(itemViewOwner.getName(), equalTo(name));
        assertThat(itemViewOwner.getDescription(), equalTo(description));
        assertThat(itemViewOwner.getAvailable(), equalTo(available));
        assertThat(itemViewOwner.getLastBooking().getBookingId(), equalTo(lastBookingId));
        assertThat(itemViewOwner.getLastBooking().getStartDate(), equalTo(lastBookingStart));
        assertThat(itemViewOwner.getLastBooking().getEndDate(), equalTo(lastBookingEnd));
        assertThat(itemViewOwner.getLastBooking().getBookingStatus(), equalTo(lastBookingStatus));
        assertThat(itemViewOwner.getLastBooking().getUser().getId(), equalTo(lastBookerId));
        assertThat(itemViewOwner.getLastBooking().getUser().getName(), equalTo(lastBookerName));
        assertThat(itemViewOwner.getNextBooking().getBookingId(), equalTo(nextBookingId));
        assertThat(itemViewOwner.getNextBooking().getStartDate(), equalTo(nextBookingStart));
        assertThat(itemViewOwner.getNextBooking().getEndDate(), equalTo(nextBookingEnd));
        assertThat(itemViewOwner.getNextBooking().getBookingStatus(), equalTo(nextBookingStatus));
        assertThat(itemViewOwner.getNextBooking().getUser().getId(), equalTo(nextBookerId));
        assertThat(itemViewOwner.getNextBooking().getUser().getName(), equalTo(nextBookerName));
    }
}
