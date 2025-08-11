package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AccessRightException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdate;
import ru.practicum.shareit.item.mapper.ItemMapperDto;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.UserService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;

    @Override
    public boolean delete(long id, long userId) {
        log.info("Delete item with id={}", id);
        checkAccessRight(id, userId);
        return itemRepository.delete(id);
    }

    @Override
    public List<ItemDto> findAll() {
        log.info("Find all items");
        return itemRepository.findAll()
                .stream()
                .map(ItemMapperDto::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> findAll(long userId) {
        log.info("Find all items by user {}", userId);
        return itemRepository.findAll()
                .stream()
                .filter(item -> item.getUserId() == userId)
                .map(ItemMapperDto::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto findById(long id) {
        log.info("Find item with id {}", id);
        Optional<Item> item = itemRepository.findById(id);
        if (item.isPresent()) {
            return ItemMapperDto.toDto(item.get());
        }
        throw new NotFoundException("Item with id " + id + " not found");
    }

    @Override
    public List<ItemDto> findByName(String name) {
        if (name.isBlank()) {
            log.warn("Name is blank");
            return List.of();
        }
        log.info("Find items by name {}", name);
        return itemRepository.findByName(name)
                .stream()
                .map(ItemMapperDto::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public Item save(ItemDto itemDto, long userId) {
        log.info("Save item {}", itemDto);
        Item item = Item.builder()
                .userId(userId)
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .build();
        userService.findById(userId);
        return itemRepository.save(item);
    }

    @Override
    public Item update(ItemUpdate newItem, Long id, Long userId) {
        log.info("Update item with id {}", id);
        Optional<Item> item = itemRepository.findById(id);
        if (item.isPresent()) {
            userService.findById(userId);
            checkAccessRight(id, userId);
            return itemRepository.update(newItem, id);
        }
        throw new NotFoundException("Item with id " + id + " not found");
    }

    private void checkAccessRight(long id, long userId) {
        Optional<Item> item = itemRepository.findById(id);
        if (item.isPresent()) {
            if (item.get().getUserId() != userId) {
                throw new AccessRightException(String.format(
                        "Access rights denied. User %d is not the owner of item %d",
                        userId,
                        id)
                );
            }
        }
    }
}
