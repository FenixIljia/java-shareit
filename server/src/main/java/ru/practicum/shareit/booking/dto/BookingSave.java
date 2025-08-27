package ru.practicum.shareit.booking.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookingSave {

    long itemId;

    LocalDateTime start;

    LocalDateTime end;
}