package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdate;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {

    public Item save(Item item);

    public List<Item> findAll();

    public boolean delete(long id);

    public List<Item> findByName(String name);

    public Optional<Item> findById(long id);

    public Item update(ItemUpdate newItem, Long id);
}