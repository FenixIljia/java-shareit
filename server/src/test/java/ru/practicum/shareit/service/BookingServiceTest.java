package ru.practicum.shareit.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingSave;
import ru.practicum.shareit.booking.dto.BookingViewDTO;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@Transactional
@ActiveProfiles("test")
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceTest {

    private final EntityManager em;
    private final BookingService bookingService;
    private final ItemService itemService;
    private final BaseServiceTest baseServiceTest;

    @Test
    public void saveBookingTest() {
        Booking booking = saveBooking();
        TypedQuery<Booking> query = em.createQuery("SELECT b FROM Booking as b WHERE b.bookingId = :bookingId", Booking.class);
        Booking bookingView = query.setParameter("bookingId", booking.getBookingId())
                .getSingleResult();
        assertThat(bookingView, equalTo(booking));
    }

    @Test
    public void findOneBookingTest() {
        Booking booking = saveBooking();
        BookingViewDTO bookingView = bookingService.findOne(booking.getBookingId(), booking.getUser().getId());
        assertThat(bookingView, notNullValue());
        assertThat(bookingView.getBookingStatus(), equalTo(booking.getBookingStatus()));
        assertThat(bookingView.getBookingId(), equalTo(booking.getBookingId()));
        assertThat(bookingView.getUser(), equalTo(booking.getUser()));
        assertThat(bookingView.getItem(), equalTo(booking.getItem()));
        assertThat(bookingView.getEndDate(), equalTo(booking.getEndDate()));
        assertThat(bookingView.getStartDate(), equalTo(booking.getStartDate()));
    }

    @Test
    public void bookingConfirmationOrRejectionTest() {
        Booking booking = saveBooking();
        bookingService.bookingConfirmationOrRejection(booking.getBookingId(), false, booking.getUser().getId());
        BookingViewDTO bookingViewDTO = bookingService.findOne(booking.getBookingId(), booking.getUser().getId());
        assertThat(bookingViewDTO.getBookingStatus(), equalTo(BookingStatus.REJECTED));
    }

    @Test
    public void findAllByUserIdTest() {
        Booking booking1 = saveBooking();
        ItemDto itemDto1 = baseServiceTest.createItemDto("Test1", "Test1", true);
        Item itemSave1 = itemService.save(itemDto1, booking1.getUser().getId());
        BookingSave booking2 = baseServiceTest.createBookingSave(
                itemSave1,
                LocalDateTime.of(2000, 1, 1, 1, 1),
                LocalDateTime.of(2000, 1, 1, 1, 2)
        );
        BookingSave booking3 = baseServiceTest.createBookingSave(
                itemSave1,
                LocalDateTime.of(2000, 1, 1, 1, 1),
                LocalDateTime.of(2000, 1, 1, 3, 4)
        );
        BookingSave booking4 = baseServiceTest.createBookingSave(
                itemSave1,
                LocalDateTime.of(2000, 1, 1, 1, 1),
                LocalDateTime.of(2000, 1, 1, 5, 6)
        );
        bookingService.save(booking2, booking1.getUser().getId());
        bookingService.save(booking3, booking1.getUser().getId());
        bookingService.save(booking4, booking1.getUser().getId());
        List<BookingViewDTO> bookingViewDTOList = bookingService.findAllByUserId(
                booking1.getUser().getId(),
                null
        );
        assertThat(bookingViewDTOList.size(), equalTo(4));
    }

    @Test
    public void findAllByOwnerId() {
        Booking booking1 = saveBooking();
        ItemDto itemDto1 = baseServiceTest.createItemDto("Test1", "Test1", true);
        Item itemSave1 = itemService.save(itemDto1, booking1.getUser().getId());
        BookingSave booking2 = baseServiceTest.createBookingSave(
                itemSave1,
                LocalDateTime.of(2000, 1, 1, 1, 1),
                LocalDateTime.of(2000, 1, 1, 1, 2)
        );
        BookingSave booking3 = baseServiceTest.createBookingSave(
                itemSave1,
                LocalDateTime.of(2000, 1, 1, 1, 1),
                LocalDateTime.of(2000, 1, 1, 3, 4)
        );
        BookingSave booking4 = baseServiceTest.createBookingSave(
                itemSave1,
                LocalDateTime.of(2000, 1, 1, 1, 1),
                LocalDateTime.of(2000, 1, 1, 5, 6)
        );
        bookingService.save(booking2, booking1.getUser().getId());
        bookingService.save(booking3, booking1.getUser().getId());
        bookingService.save(booking4, booking1.getUser().getId());
        List<BookingViewDTO> bookingViewDTOList = bookingService.findAllByOwnerId(
                itemSave1.getUser().getId(),
                null);
        assertThat(bookingViewDTOList.size(), equalTo(4));
    }

    private Booking saveBooking() {
        User user = baseServiceTest.saveUser("admin", "admin");
        Item item = baseServiceTest.saveItem(user, "Test", "Test bookingServiceSave", true);
        BookingSave bookingSave = baseServiceTest.createBookingSave(
                item,
                LocalDateTime.of(2000, 10, 2, 2, 2),
                LocalDateTime.of(2000, 11, 2, 2, 2)
        );
        return bookingService.save(bookingSave, user.getId());
    }
}
