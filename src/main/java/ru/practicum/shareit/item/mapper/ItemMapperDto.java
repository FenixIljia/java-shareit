package ru.practicum.shareit.item.mapper;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemDto;

@Slf4j
public class ItemMapperDto {
    public static ItemDto toDto(Item item) {
        log.trace("ItemMapperDto.toDto: {}", item.getName());
        return ItemDto
                .builder()
                .itemId(item.getItemId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }
}
