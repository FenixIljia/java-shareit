package ru.practicum.shareit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingSave;
import ru.practicum.shareit.booking.dto.BookingViewDTO;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.user.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private BookingService bookingService;

    @MockBean
    private ItemService itemService;

    @Autowired
    private MockMvc mvc;

    private User user = new User(1L, "test", "test");

    private Item item = new Item(1L, user, "test", "test", true, null);

    private Booking booking = new Booking(
            1L,
            user,
            item,
            LocalDateTime.of(2000, 10, 10, 10, 1),
            LocalDateTime.of(2000, 10, 10, 10, 2),
            BookingStatus.WAITING
    );

    @Test
    public void createTest() throws Exception {
        when(bookingService.save(any(BookingSave.class), anyLong()))
                .thenReturn(booking);
        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", user.getId())
                        .content(mapper.writeValueAsString(booking))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.booker.name", is(booking.getUser().getName()), String.class))
                .andExpect(jsonPath("$.booker.email", is(booking.getUser().getEmail()), String.class));
    }

    @Test
    public void bookingConfirmationOrRejectionTest() throws Exception {
        Booking updatedBooking = booking.toBuilder().bookingStatus(BookingStatus.REJECTED).build();
        when(bookingService.bookingConfirmationOrRejection(anyLong(), anyBoolean(), anyLong()))
                .thenReturn(updatedBooking);
        mvc.perform(patch("/bookings/" + booking.getBookingId())
                        .header("X-Sharer-User-Id", user.getId())
                        .param("approved", "false")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value(BookingStatus.REJECTED.name()));
    }

    @Test
    public void findByBookingIdTest() throws Exception {
        BookingViewDTO bookingViewDTO = new BookingViewDTO();
        bookingViewDTO.setBookingId(booking.getBookingId());
        bookingViewDTO.setItem(item);
        bookingViewDTO.setUser(user);
        bookingViewDTO.setEndDate(booking.getEndDate());
        bookingViewDTO.setStartDate(booking.getStartDate());
        bookingViewDTO.setBookingStatus(BookingStatus.REJECTED);
        when(bookingService.findOne(anyLong(), anyLong()))
                .thenReturn(bookingViewDTO);
        mvc.perform(get("/bookings/" + booking.getBookingId())
                        .header("X-Sharer-User-Id", user.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.booker.name", is(booking.getUser().getName()), String.class))
                .andExpect(jsonPath("$.booker.email", is(booking.getUser().getEmail()), String.class))
                .andExpect(jsonPath("$.end", is("2000-10-10T10:02:00")))
                .andExpect(jsonPath("$.start", is("2000-10-10T10:01:00")))
                .andExpect(jsonPath("$.status", is(BookingStatus.REJECTED.name())));
    }

    @Test
    public void findAllByUserIdTest() throws Exception {
        when(bookingService.findAllByUserId(anyLong(), any(BookingStatus.class)))
                .thenReturn(creteListBookingViewDTO());
        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", user.getId())
                        .param("status", BookingStatus.APPROVED.name())
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(3L), Long.class));
    }

    @Test
    public void findAllByOwnerIdTest() throws Exception {
        when(bookingService.findAllByOwnerId(anyLong(), any(BookingStatus.class)))
                .thenReturn(creteListBookingViewDTO());
        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", user.getId())
                        .param("status", BookingStatus.APPROVED.name())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(3L), Long.class));
    }

    private List<BookingViewDTO> creteListBookingViewDTO() {
        BookingViewDTO bookingViewDTO1 = new BookingViewDTO();
        bookingViewDTO1.setBookingId(booking.getBookingId());
        bookingViewDTO1.setItem(item);
        bookingViewDTO1.setUser(user);
        bookingViewDTO1.setEndDate(booking.getEndDate());
        bookingViewDTO1.setStartDate(booking.getStartDate());
        BookingViewDTO bookingViewDTO2 = new BookingViewDTO();
        bookingViewDTO2.setBookingId(booking.getBookingId());
        bookingViewDTO2.setItem(item);
        bookingViewDTO2.setUser(user);
        bookingViewDTO2.setEndDate(booking.getEndDate());
        bookingViewDTO2.setStartDate(booking.getStartDate());
        BookingViewDTO bookingViewDTO3 = new BookingViewDTO();
        bookingViewDTO3.setBookingId(booking.getBookingId());
        bookingViewDTO3.setItem(item);
        bookingViewDTO3.setUser(user);
        bookingViewDTO3.setEndDate(booking.getEndDate());
        bookingViewDTO3.setStartDate(booking.getStartDate());
        return List.of(bookingViewDTO1, bookingViewDTO2, bookingViewDTO3);
    }
}
