package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdate;

import java.util.List;

public interface ItemService {

    Item save(ItemDto item, long userId);

    List<ItemDto> findAll();

    List<ItemDto> findAll(long userId);

    boolean delete(long id, long userId);

    List<ItemDto> findByName(String name);

    ItemDto findById(long id);

    Item update(ItemUpdate newItem, Long id, Long userId);
}