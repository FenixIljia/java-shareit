package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;

import java.util.List;

@RestController
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public List<ItemViewOwner> getItemsByUser(@RequestHeader(name = "X-Sharer-User-Id") long userId) {
        return itemService.findAllByUserId(userId);
    }

    @GetMapping("/{itemId}")
    public ItemViewOwner getItemById(
            @PathVariable("itemId") long itemId,
            @RequestHeader(name = "X-Sharer-User-Id") long userId
    ) {
        return itemService.findById(itemId, userId);
    }

    @GetMapping("/search")
    public List<ItemView> getItemByName(@RequestParam String text) {
        return itemService.findAllByName(text);
    }

    @PostMapping
    public Item create(
            @RequestBody ItemDto itemDto,
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

    @PostMapping("/{itemId}/comment")
    public CommentView save(
            @RequestBody SaveComment saveComment,
            @PathVariable("itemId") long itemId,
            @RequestHeader("X-Sharer-User-Id") long userId
    ) {
        return itemService.save(saveComment, itemId, userId);
    }
}
