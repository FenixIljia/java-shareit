package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestViewDto;
import ru.practicum.shareit.request.dto.SaveItemRequestDto;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@AllArgsConstructor
public class ItemRequestController {

    private final ItemRequestsService itemRequestsService;

    @GetMapping("/all")
    public List<ItemRequestViewDto> findAll() {
        return itemRequestsService.findAll();
    }

    @GetMapping("/{requestId}")
    public ItemRequestViewDto findAllByRequestId(@PathVariable Long requestId) {
        return itemRequestsService.findById(requestId);
    }

    @GetMapping
    public List<ItemRequestViewDto> findAllByUserId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestsService.findAllByUserId(userId);
    }

    @PostMapping
    public ItemRequest save(
            @RequestBody SaveItemRequestDto saveItemRequestDto,
            @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        saveItemRequestDto.setUserId(userId);
        return itemRequestsService.save(saveItemRequestDto);
    }
}
