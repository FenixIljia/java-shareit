package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.constant.HttpHeaders;
import ru.practicum.shareit.item.dto.ItemSaveDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.dto.SaveComment;

@RestController
@AllArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> getItemsByUserId(@RequestHeader(name = HttpHeaders.USER_ID_HEADER) long userId) {
        return itemClient.findAllByUserId(userId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(
            @PathVariable long itemId,
            @RequestHeader(name = HttpHeaders.USER_ID_HEADER) long userId
    ) {
        return itemClient.findItemById(itemId, userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getAllByName(@RequestParam String text) {
        return itemClient.getAllByName(text);
    }

    @PostMapping
    public ResponseEntity<Object> save(
            @Valid @RequestBody ItemSaveDto itemSaveDto,
            @RequestHeader(HttpHeaders.USER_ID_HEADER) long userId
    ) {
        return itemClient.save(itemSaveDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(
            @PathVariable long itemId,
            @RequestBody ItemUpdateDto itemUpdateDto,
            @RequestHeader(HttpHeaders.USER_ID_HEADER) long userId
    ) {
        return itemClient.update(itemId, itemUpdateDto, userId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> save(
            @RequestBody SaveComment saveComment,
            @PathVariable long itemId,
            @RequestHeader(HttpHeaders.USER_ID_HEADER) long userId
    ) {
        return itemClient.save(saveComment, itemId, userId);
    }
}
