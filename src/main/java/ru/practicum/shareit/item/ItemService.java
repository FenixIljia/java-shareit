package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.*;

import java.util.List;

public interface ItemService {

    Item save(ItemDto item, long userId);

    List<ItemViewOwner> findAllByUserId(long useкId);

    void delete(long id, long userId);

    List<ItemView> findAllByName(String name);

    ItemViewOwner findById(long id, long userId);

    Item update(ItemUpdate newItem, long id, Long userId);

    CommentView save(SaveComment saveComment, long itemId, Long userId);
}