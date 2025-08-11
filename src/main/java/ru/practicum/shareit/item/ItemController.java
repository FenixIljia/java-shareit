package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdate;

import java.util.List;

@RestController
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public List<ItemDto> getItems(@RequestHeader(name = "X-Sharer-User-Id", defaultValue = "-1") long userId) {
        if (userId == -1) {
            return itemService.findAll();
        } else {
            return itemService.findAll(userId);
        }
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable("itemId") long id) {
        return itemService.findById(id);
    }

    @GetMapping("/search")
    public List<ItemDto> getItemByName(@RequestParam String text) {
        return itemService.findByName(text);
    }

    @PostMapping
    public Item create(
            @RequestBody @Valid ItemDto itemDto,
            @RequestHeader("X-Sharer-User-Id") long userId
    ) {
        return itemService.save(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public Item update(
            @PathVariable long itemId,
            @RequestBody ItemUpdate itemUpdate,
            @RequestHeader("X-Sharer-User-Id") long userId
    ) {
        return itemService.update(itemUpdate, itemId, userId);
    }


}
