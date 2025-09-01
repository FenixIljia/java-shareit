package ru.practicum.shareit.service;

import jakarta.transaction.Transactional;
import lombok.Data;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Service;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.booking.dto.BookingSave;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;

@Transactional
@ActiveProfiles("test")
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE
)
@Data
@Service
public class BaseServiceTest {

    private final ItemService itemService;
    private final UserService userService;

    public User saveUser(String email, String name) {
        User user = createUser(name, email);
        return userService.save(user);
    }

    public Item saveItem(User user, String itemName, String description, Boolean available) {
        return itemService.save(createItemDto(itemName, description, available), user.getId());
    }

    public BookingSave createBookingSave(Item item, LocalDateTime start, LocalDateTime end) {
        BookingSave bookingSave = new BookingSave();
        bookingSave.setStart(start);
        bookingSave.setEnd(end);
        bookingSave.setItemId(item.getItemId());
        return bookingSave;
    }

    public User createUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        return user;
    }

    public ItemDto createItemDto(String name, String description, Boolean available) {
        ItemDto itemDto = new ItemDto();
        itemDto.setName(name);
        itemDto.setDescription(description);
        itemDto.setAvailable(available);
        return itemDto;
    }
}
