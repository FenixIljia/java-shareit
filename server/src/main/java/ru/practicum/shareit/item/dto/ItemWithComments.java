package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Data
@NoArgsConstructor
public class ItemWithComments {
    private Long itemId;
    private UserDto user;
    private String name;
    private String description;
    private Boolean available;
    private List<CommentView> comment;
}