package ru.practicum.shareit.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookingSave;
import ru.practicum.shareit.exception.AccessRightException;
import ru.practicum.shareit.exception.NotCompletedBooking;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestsService;
import ru.practicum.shareit.request.dto.SaveItemRequestDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceTest {

    private final ItemService itemService;
    private final BaseServiceTest baseServiceTest;
    private final EntityManager em;
    private final BookingService bookingService;
    private final ItemRequestsService itemRequestsService;

    @Test
    public void saveItemTest() {
        User user = baseServiceTest.saveUser("test@mail.ru", "Test");
        ItemDto itemDto = baseServiceTest.createItemDto("Test", "Test", true);
        Item item = itemService.save(itemDto, user.getId());
        assertThat(item, notNullValue());
    }

    @Test
    public void saveItemWithInvalidUserIdTest() {
        User user = baseServiceTest.saveUser("test@mail.ru", "Test");
        ItemDto itemDto = baseServiceTest.createItemDto("Test", "Test", true);
        long invalidUserId = user.getId() + 1;
        NotFoundException exception = assertThrows(NotFoundException.class, () -> itemService.save(itemDto, invalidUserId));
        assertThat(exception, notNullValue());
        assertThat(exception.getMessage(), equalTo("User with id " + invalidUserId + " not found"));
    }

    @Test
    public void saveItemWithRequestTest() {
        User user1 = baseServiceTest.saveUser("test@mail.ru", "Test");
        ItemDto itemDto = baseServiceTest.createItemDto("Test", "Test", true);
        User user2 = baseServiceTest.saveUser("test2@mail.ru", "Test2");
        SaveItemRequestDto itemRequest = new SaveItemRequestDto(user2.getId(), "test");
        ItemRequest itemRequestAfterSave = itemRequestsService.save(itemRequest);
        itemDto.setRequestId(itemRequestAfterSave.getId());
        long id = itemService.save(itemDto, user1.getId()).getItemId();
        TypedQuery<Item> query = em.createQuery("SELECT i FROM Item i WHERE itemId = :itemId", Item.class);
        Item item = query.setParameter("itemId", id).getSingleResult();
        assertThat(item, notNullValue());
        assertThat(id, equalTo(item.getItemId()));
        assertThat(user1.getId(), equalTo(item.getUser().getId()));
        assertThat(item.getName(), equalTo("Test"));
        assertThat(item.getRequest().getId(), equalTo(itemDto.getRequestId()));
        assertThat(item.getDescription(), equalTo(item.getDescription()));
        assertThat(item.getAvailable(), equalTo(item.getAvailable()));
    }

    @Test
    public void saveItemWithBookingTest() {

        User user = baseServiceTest.saveUser("test@mail.ru", "Test");

        User userBooker = baseServiceTest.saveUser("test2@mail.ru", "Test2");

        ItemDto itemDto = baseServiceTest.createItemDto("Test", "Test", true);

        Item item = itemService.save(itemDto, user.getId());

        BookingSave bookingSaveFuture = new BookingSave();
        bookingSaveFuture.setItemId(item.getItemId());
        bookingSaveFuture.setStart(LocalDateTime.now().plusDays(1));
        bookingSaveFuture.setEnd(LocalDateTime.now().plusDays(2));

        BookingSave bookingSaveFuture2 = new BookingSave();
        bookingSaveFuture2.setItemId(item.getItemId());
        bookingSaveFuture2.setStart(LocalDateTime.now().plusDays(3));
        bookingSaveFuture2.setEnd(LocalDateTime.now().plusDays(4));

        BookingSave bookingSavePast = new BookingSave();
        bookingSavePast.setItemId(item.getItemId());
        bookingSavePast.setStart(LocalDateTime.now().minusDays(2));
        bookingSavePast.setEnd(LocalDateTime.now().minusDays(1));

        BookingSave bookingSavePast2 = new BookingSave();
        bookingSavePast2.setItemId(item.getItemId());
        bookingSavePast2.setStart(LocalDateTime.now().minusDays(4));
        bookingSavePast2.setEnd(LocalDateTime.now().minusDays(3));

        bookingService.save(bookingSaveFuture, userBooker.getId());
        bookingService.save(bookingSavePast, userBooker.getId());
        bookingService.save(bookingSaveFuture2, userBooker.getId());
        bookingService.save(bookingSavePast2, userBooker.getId());

        List<ItemViewOwner> itemViewOwnerList = itemService.findAllByUserId(user.getId());
        ItemViewOwner itemViewOwner = itemViewOwnerList.getFirst();

        assertThat(itemViewOwner, notNullValue());
        assertThat(itemViewOwner.getItemId(), equalTo(item.getItemId()));
        assertThat(itemViewOwner.getOwnerId(), equalTo(user.getId()));
        assertThat(itemViewOwner.getName(), equalTo("Test"));
        assertThat(itemViewOwner.getDescription(), equalTo("Test"));
        assertThat(itemViewOwner.getAvailable(), equalTo(item.getAvailable()));
        assertThat(
                itemViewOwner.getNextBooking().getStartDate().truncatedTo(ChronoUnit.MILLIS),
                equalTo(bookingSaveFuture.getStart().truncatedTo(ChronoUnit.MILLIS)
                ));
        assertThat(
                itemViewOwner.getNextBooking().getEndDate().truncatedTo(ChronoUnit.MILLIS),
                equalTo(bookingSaveFuture.getEnd().truncatedTo(ChronoUnit.MILLIS)
                ));
        assertThat(
                itemViewOwner.getLastBooking().getStartDate().toEpochSecond(ZoneOffset.UTC),
                equalTo(bookingSavePast.getStart().toEpochSecond(ZoneOffset.UTC))
        );
        assertThat(
                itemViewOwner.getLastBooking().getEndDate().toEpochSecond(ZoneOffset.UTC),
                equalTo(bookingSavePast.getEnd().toEpochSecond(ZoneOffset.UTC))
        );
    }

    @Test
    public void deleteItemTest() {
        User user = baseServiceTest.saveUser("test@mail.ru", "Test");
        Item item = baseServiceTest.saveItem(user, "Test", "Test", true);
        itemService.delete(item.getItemId(), user.getId());
        TypedQuery<Item> query = em.createQuery("SELECT i FROM Item i WHERE i.id = :id", Item.class);
        List<Item> items = query.setParameter("id", item.getItemId()).getResultList();
        assertThat(items, empty());
    }

    @Test
    public void findAllByUserIdTest() {
        User user = baseServiceTest.saveUser("test@mail.ru", "Test");
        ItemDto itemDto1 = baseServiceTest.createItemDto("Test1", "Test", true);
        ItemDto itemDto2 = baseServiceTest.createItemDto("Test2", "Test", true);
        ItemDto itemDto3 = baseServiceTest.createItemDto("Test3", "Test", true);
        ItemDto itemDto4 = baseServiceTest.createItemDto("Test4", "Test", true);
        itemService.save(itemDto1, user.getId());
        itemService.save(itemDto2, user.getId());
        itemService.save(itemDto3, user.getId());
        itemService.save(itemDto4, user.getId());
        List<ItemViewOwner> itemViewOwnerList = itemService.findAllByUserId(user.getId());
        assertThat(itemViewOwnerList.size(), equalTo(4));
    }

    @Test
    public void findByIdTest() {
        User user = baseServiceTest.saveUser("test@mail.ru", "Test");
        Item item = baseServiceTest.saveItem(user, "Test", "Test", true);
        ItemViewOwner itemViewOwner = itemService.findById(item.getItemId(), user.getId());
        assertThat(itemViewOwner, notNullValue());
        assertThat(itemViewOwner.getItemId(), equalTo(item.getItemId()));
        assertThat(itemViewOwner.getOwnerId(), equalTo(item.getUser().getId()));
        assertThat(itemViewOwner.getAvailable(), equalTo(item.getAvailable()));
        assertThat(itemViewOwner.getName(), equalTo(item.getName()));
        assertThat(itemViewOwner.getDescription(), equalTo(item.getDescription()));
        assertThat(itemViewOwner.getComments(), empty());
        assertThat(itemViewOwner.getLastBooking(), equalTo(null));
        assertThat(itemViewOwner.getNextBooking(), equalTo(null));
    }

    @Test
    public void findByIdWithInvalidIdTest() {
        User user = baseServiceTest.saveUser("test@mail.ru", "Test");
        Item item = baseServiceTest.saveItem(user, "Test", "Test", true);
        long invalidId = item.getItemId() + 1;
        NotFoundException exception = assertThrows(NotFoundException.class, () -> itemService.findById(invalidId, user.getId()));
        assertThat(exception, notNullValue());
        assertThat("Item with id " + invalidId + " not found", equalTo(exception.getMessage()));
    }

    @Test
    public void findAllByNameTest() {
        User user = baseServiceTest.saveUser("test@mail.ru", "Test");
        ItemDto itemDto1 = baseServiceTest.createItemDto("Test", "null", true);
        ItemDto itemDto2 = baseServiceTest.createItemDto("Test in", "null", true);
        ItemDto itemDto3 = baseServiceTest.createItemDto("in Test", "null", true);
        ItemDto itemDto4 = baseServiceTest.createItemDto("test", "null", true);
        ItemDto itemDto5 = baseServiceTest.createItemDto("null", "Test", true);
        ItemDto itemDto6 = baseServiceTest.createItemDto("null", "test", true);
        itemService.save(itemDto1, user.getId());
        itemService.save(itemDto2, user.getId());
        itemService.save(itemDto3, user.getId());
        itemService.save(itemDto4, user.getId());
        itemService.save(itemDto5, user.getId());
        itemService.save(itemDto6, user.getId());
        List<ItemView> itemViewOwnerList = itemService.findAllByName("Test");
        assertThat(itemViewOwnerList.size(), equalTo(6));
    }

    @Test
    public void findAllByNameWithInvalidNameTest() {
        User user = baseServiceTest.saveUser("test@mail.ru", "Test");
        ItemDto itemDto = baseServiceTest.createItemDto("Test", "null", true);
        itemService.save(itemDto, user.getId());
        List<ItemView> itemViewOwnerList = itemService.findAllByName("");
        assertThat(itemViewOwnerList.size(), equalTo(0));
    }

    @Test
    public void updateItemTest() {
        User user = baseServiceTest.saveUser("test@mail.ru", "Test");
        Item item = baseServiceTest.saveItem(user, "Test", "Test", true);
        ItemUpdate itemUpdate = new ItemUpdate();
        itemUpdate.setName("Updated Name");
        itemUpdate.setDescription("Updated Description");
        itemUpdate.setAvailable(true);
        itemService.update(itemUpdate, item.getItemId(), user.getId());
        TypedQuery<Item> query = em.createQuery("SELECT i FROM Item i WHERE i.id = :id", Item.class);
        Item itemQuery = query.setParameter("id", item.getItemId()).getSingleResult();
        assertThat(itemQuery, notNullValue());
        assertThat(itemQuery.getName(), equalTo("Updated Name"));
        assertThat(itemQuery.getDescription(), equalTo("Updated Description"));
    }

    @Test
    public void updateItemWithInvalidUserIdTest() {
        User user = baseServiceTest.saveUser("test@mail.ru", "Test");
        Item item = baseServiceTest.saveItem(user, "Test", "Test", true);
        ItemUpdate itemUpdate = new ItemUpdate();
        itemUpdate.setName("Updated Name");
        itemUpdate.setDescription("Updated Description");
        itemUpdate.setAvailable(true);
        long invalidUserId = user.getId() + 1;
        AccessRightException exception = assertThrows(AccessRightException.class, () -> itemService.update(itemUpdate, item.getItemId(), invalidUserId));
        assertThat(exception, notNullValue());
        assertThat(exception.getMessage(), equalTo(
                "Access rights denied. User " + invalidUserId + " is not the owner of item " + item.getItemId()
        ));
    }

    @Test
    public void updateItemWithInvalidItemIdTest() {
        User user = baseServiceTest.saveUser("test@mail.ru", "Test");
        Item item = baseServiceTest.saveItem(user, "Test", "Test", true);
        ItemUpdate itemUpdate = new ItemUpdate();
        itemUpdate.setName("Updated Name");
        itemUpdate.setDescription("Updated Description");
        itemUpdate.setAvailable(true);
        long invalidItemId = item.getItemId() + 1;
        NotFoundException exception = assertThrows(NotFoundException.class, () -> itemService.update(itemUpdate, invalidItemId, user.getId()));
        assertThat(exception, notNullValue());
        assertThat(exception.getMessage(), equalTo("Item with id " + invalidItemId + " not found"));
    }

    @Test
    public void updateItemWithNullFieldsTest() {
        User user = baseServiceTest.saveUser("test@mail.ru", "Test");
        Item item = baseServiceTest.saveItem(user, "Test", "Test", true);
        ItemUpdate itemUpdate = new ItemUpdate();
        itemService.update(itemUpdate, item.getItemId(), user.getId());
        TypedQuery<Item> query = em.createQuery("SELECT i FROM Item i WHERE i.id = :id", Item.class);
        Item itemAfterUpdate = query.setParameter("id", item.getItemId()).getSingleResult();
        assertThat(itemAfterUpdate, notNullValue());
        assertThat(itemAfterUpdate.getName(), equalTo("Test"));
        assertThat(itemAfterUpdate.getDescription(), equalTo("Test"));
        assertThat(itemAfterUpdate.getAvailable(), equalTo(true));
    }

    @Test
    public void commentSaveTest() {
        User user = baseServiceTest.saveUser("test@mail.ru", "Test");
        Item item = baseServiceTest.saveItem(user, "Test", "Test", true);
        BookingSave bookingSave = baseServiceTest.createBookingSave(
                item,
                LocalDateTime.of(2000, 10, 1, 1, 1),
                LocalDateTime.of(2000, 10, 1, 1, 2)
        );
        bookingService.save(bookingSave, user.getId());
        SaveComment saveComment = new SaveComment("Test with item.id + " + item.getItemId());
        itemService.save(saveComment, item.getItemId(), user.getId());
        TypedQuery<Comment> query = em.createQuery("SELECT c FROM Comment c WHERE c.item.id = :id", Comment.class);
        Comment commentQuery = query.setParameter("id", item.getItemId()).getSingleResult();
        assertThat(commentQuery, notNullValue());
        assertThat(commentQuery.getText(), equalTo(saveComment.getText()));
    }

    @Test
    public void commentSaveWithInvalidRentedTest() {
        User user = baseServiceTest.saveUser("test@mail.ru", "Test");
        Item item = baseServiceTest.saveItem(user, "Test", "Test", true);
        SaveComment saveComment = new SaveComment("Test with item.id + " + item.getItemId());
        AccessRightException exception = assertThrows(AccessRightException.class, () -> itemService.save(saveComment, item.getItemId(), user.getId()));
        assertThat(exception, notNullValue());
        assertThat(exception.getMessage(), equalTo("User with id " + user.getId() + " not booked item with id " + item.getItemId()));
    }

    @Test
    public void commentSaveWithInvalidRentedFutureTest() {
        User user = baseServiceTest.saveUser("test@mail.ru", "Test");
        Item item = baseServiceTest.saveItem(user, "Test", "Test", true);
        LocalDateTime now = LocalDateTime.now();
        BookingSave bookingSave = baseServiceTest.createBookingSave(
                item,
                now,
                now.plusDays(1)
        );
        bookingService.save(bookingSave, user.getId());
        SaveComment saveComment = new SaveComment("Test with item.id + " + item.getItemId());
        NotCompletedBooking exception = assertThrows(NotCompletedBooking.class, () -> itemService.save(saveComment, item.getItemId(), user.getId()));
        assertThat(exception, notNullValue());
        assertThat(exception.getMessage(), equalTo("User with id " + user.getId() + " not has been rented for item with id " + item.getItemId()));
    }
}
