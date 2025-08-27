package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestViewDto;
import ru.practicum.shareit.request.dto.SaveItemRequestDto;

import java.util.List;

interface ItemRequestsService {

    ItemRequest save(SaveItemRequestDto req);

    List<ItemRequestViewDto> findAllByUserId(Long userId);

    List<ItemRequestViewDto> findAll();

    ItemRequestViewDto findById(Long id);
}
