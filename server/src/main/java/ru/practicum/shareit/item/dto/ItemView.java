package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public interface ItemView {
    @JsonProperty("id")
    Long getItemId();

    String getName();

    String getDescription();

    Boolean getAvailable();
}
