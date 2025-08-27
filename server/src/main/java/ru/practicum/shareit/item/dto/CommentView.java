package ru.practicum.shareit.item.dto;

import lombok.*;

import java.time.LocalDateTime;

@Builder
@ToString
@AllArgsConstructor
@Data
@NoArgsConstructor
public class CommentView {

    private Long id;

    private String authorName;

    private String text;

    private Long itemId;

    private LocalDateTime created;
}
