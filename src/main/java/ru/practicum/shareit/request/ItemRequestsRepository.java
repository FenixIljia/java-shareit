package ru.practicum.shareit.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.request.dto.ItemRequestViewDto;

import java.util.List;
import java.util.Optional;

public interface ItemRequestsRepository extends JpaRepository<ItemRequest, Long> {

    @Query("SELECT new ru.practicum.shareit.request.dto.ItemRequestViewDto(" +
            "req.id, " +
            "req.description, " +
            "req.createDate" +
            ") " +
            "FROM ItemRequest req " +
            "WHERE req.userId.id = :userId " +
            "ORDER BY req.createDate ASC")
    List<ItemRequestViewDto> findAllByUserIdOrderByCreateDate(@Param("userId") Long userId);

    @Query("SELECT new ru.practicum.shareit.request.dto.ItemRequestViewDto(" +
            "req.id, " +
            "req.description," +
            "req.createDate" +
            ") " +
            "FROM ItemRequest req WHERE req.id = :id"
    )
    Optional<ItemRequestViewDto> findByRequestId(@Param("id") Long id);

    @Query("SELECT new ru.practicum.shareit.request.dto.ItemRequestViewDto(" +
            "req.id, " +
            "req.description, " +
            "req.createDate" +
            ") " +
            "FROM ItemRequest req " +
            "ORDER BY req.createDate ASC")
    List<ItemRequestViewDto> findAllOrderByCreateDate();
}
