package ru.practicum.shareit.service;

import ru.practicum.shareit.booking.dto.BookingSave;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

public class BaseServiceTest {

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
