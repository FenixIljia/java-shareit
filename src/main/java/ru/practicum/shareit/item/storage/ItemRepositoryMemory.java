package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemUpdate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class ItemRepositoryMemory implements ItemRepository {
    private final List<Item> items =  new ArrayList<>();
    private long id = 0L;

    @Override
    public List<Item> findAll() {
        return items;
    }

    @Override
    public Optional<Item> findById(long id) {
        return items
                .stream()
                .filter(item -> item.getId() == id)
                .findFirst();
    }

    @Override
    public Item save(Item item) {
        item.setId(generateId());
        items.add(item);
        return item;
    }

    @Override
    public List<Item> findByName(String name) {
        return items
                .stream()
                .filter(item -> (item.getName().toLowerCase().contains(name.toLowerCase()) || item.getDescription().contains(name))
                        && item.getAvailable() == true)
                .collect(Collectors.toList()
        );
    }

    @Override
    public boolean delete(long id) {
        return items.removeIf(item -> item.getId() == id);
    }

    @Override
    public Item update(ItemUpdate newItem, Long id) {
        Item oldItem = items
                .stream()
                .filter(item1 -> Objects.equals(item1.getId(), id))
                .findFirst()
                .get();
        if (newItem.getName() != null) {
            oldItem.setName(newItem.getName());
        }
        if (newItem.getDescription() != null) {
            oldItem.setDescription(newItem.getDescription());
        }
        if (newItem.getAvailable() != null) {
            oldItem.setAvailable(newItem.getAvailable());
        }
        return oldItem;
    }

    private Long generateId() {
        return id++;
    }
}