package ru.practicum.shareit.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
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
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;

@Transactional
@ActiveProfiles("test")
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BookingServiceTest {

    private final EntityManager em;
    private final BookingService bookingService;
    private final ItemService itemService;
    private final BaseServiceTest baseServiceTest = new BaseServiceTest();
    @Autowired
    private UserService userService;

    @BeforeEach
    @AfterEach
    public void cleanDatabase() {
        // Очистка в правильном порядке (из-за foreign key constraints)
        em.createNativeQuery("DELETE FROM bookings").executeUpdate();
        em.createNativeQuery("DELETE FROM comments").executeUpdate();
        em.createNativeQuery("DELETE FROM items").executeUpdate();
        em.createNativeQuery("DELETE FROM requests").executeUpdate();
        em.createNativeQuery("DELETE FROM users").executeUpdate();

        // Сброс последовательностей для H2
        em.createNativeQuery("ALTER TABLE users ALTER COLUMN user_id RESTART WITH 1").executeUpdate();
        em.createNativeQuery("ALTER TABLE items ALTER COLUMN item_id RESTART WITH 1").executeUpdate();
        em.createNativeQuery("ALTER TABLE bookings ALTER COLUMN booking_id RESTART WITH 1").executeUpdate();

        em.flush();
        em.clear();
    }

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

    @Test
    public void deleteByUserId() {
        User user = userService.save(baseServiceTest.createUser("Test", "test@mail.ru"));

        Item item = itemService.save(baseServiceTest.createItemDto("Test", "Test", true), user.getId());

        BookingSave bookingSave = new BookingSave();
        bookingSave.setItemId(item.getItemId());
        bookingSave.setStart(LocalDateTime.now().plusDays(1));
        bookingSave.setEnd(LocalDateTime.now().plusDays(2));

        Booking bookingAfterSave = bookingService.save(bookingSave, user.getId());

        // Явный flush перед удалением
        em.flush();

        bookingService.deleteByUserId(bookingAfterSave.getBookingId());

        // Явный flush после удаления
        em.flush();
        em.clear(); // Очищаем кэш Hibernate

        Query query = em.createQuery("SELECT b FROM Booking b WHERE b.bookingId = :bookingId", Booking.class);
        query.setParameter("bookingId", bookingAfterSave.getBookingId());
        List<Booking> bookingList = query.getResultList();

        assertThat(bookingList, empty());
    }

    private Booking saveBooking() {
        User user = userService.save(baseServiceTest.createUser("admin", "test@mail.ru"));
        Item item = itemService.save(baseServiceTest.createItemDto("Test", "Test", true), user.getId());
        BookingSave bookingSave = baseServiceTest.createBookingSave(
                item,
                LocalDateTime.of(2000, 10, 2, 2, 2),
                LocalDateTime.of(2000, 11, 2, 2, 2)
        );
        return bookingService.save(bookingSave, user.getId());
    }
}
