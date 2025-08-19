package ru.practicum.shareit.item.mapper;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.dto.CommentView;

@Slf4j
public class CommentMapperDto {
    public static CommentView toViewDto(Comment comment) {
        return CommentView.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getUser().getName())
                .itemId(comment.getItem().getItemId())
                .created(comment.getCreated())
                .build();
    }
}
