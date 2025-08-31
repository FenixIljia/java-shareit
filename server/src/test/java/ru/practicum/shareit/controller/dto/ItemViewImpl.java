package ru.practicum.shareit.controller.dto;

import ru.practicum.shareit.item.dto.ItemView;

public class ItemViewImpl implements ItemView {
    private final Long itemId;
    private final String name;
    private final String description;
    private final Boolean available;

    public ItemViewImpl(Long itemId, String name, String description, Boolean available) {
        this.itemId = itemId;
        this.name = name;
        this.description = description;
        this.available = available;
    }

    @Override
    public Long getItemId() {
        return itemId;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public Boolean getAvailable() {
        return available;
    }
}