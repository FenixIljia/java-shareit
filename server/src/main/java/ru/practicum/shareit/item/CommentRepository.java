package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.dto.CommentView;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("SELECT new ru.practicum.shareit.item.dto.CommentView(c.id, c.user.name, c.text, c.item.itemId, c.created)" +
            " FROM Comment c WHERE c.item.itemId = :itemId")
    List<CommentView> findAllByItemId(Long itemId);
}
