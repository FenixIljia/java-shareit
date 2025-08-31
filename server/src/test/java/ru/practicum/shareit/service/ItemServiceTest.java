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
import ru.practicum.shareit.booking.dto.BookingSave;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;

@Transactional
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceTest {

    private final ItemService itemService;
    private final BaseServiceTest baseServiceTest;
    private final EntityManager em;
    private UserService userService;
    private final BookingService bookingService;

    @Test
    public void saveItemTest() {
        User user = baseServiceTest.saveUser("test@mail.ru", "Test");
        ItemDto itemDto = baseServiceTest.createItemDto("Test", "Test", true);
        Item item = itemService.save(itemDto, user.getId());
        assertThat(item, notNullValue());
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
}
