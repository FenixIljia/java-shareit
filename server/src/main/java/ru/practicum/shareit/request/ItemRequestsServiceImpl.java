package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestViewDto;
import ru.practicum.shareit.request.dto.ResponseViewDto;
import ru.practicum.shareit.request.dto.SaveItemRequestDto;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@Slf4j
@AllArgsConstructor
public class ItemRequestsServiceImpl implements ItemRequestsService {

    private final ItemRequestsRepository itemRequestsRepository;

    private final UserRepository userRepository;

    private final ItemRepository itemRepository;

    @Override
    public ItemRequest save(SaveItemRequestDto req) {
        log.info("Save ItemRequest");
        ItemRequest itemRequest = ItemRequest.builder()
                .description(req.getDescription())
                .userId(userRepository.findById(req.getUserId()).get())
                .build();
        return itemRequestsRepository.save(itemRequest);
    }

    @Override
    public ItemRequestViewDto findById(Long id) {
        log.info("Find ItemRequest by Id {}", id);
        Optional<ItemRequestViewDto> itemRequestViewDto = itemRequestsRepository.findByRequestId(id);
        if (itemRequestViewDto.isEmpty()) {
            throw new NotFoundException("ItemRequest with id " + id + " not found");
        }
        return addResponse(itemRequestViewDto.get());
    }

    @Override
    public List<ItemRequestViewDto> findAllByUserId(Long userId) {
        log.info("Find All ItemRequest by UserId {}", userId);
        List<ItemRequestViewDto> itemRequestViewDto = itemRequestsRepository.findAllByUserIdOrderByCreateDate(userId);
        for (ItemRequestViewDto requestViewDto : itemRequestViewDto) {
            addResponse(requestViewDto);
        }
        return itemRequestViewDto;
    }

    @Override
    public List<ItemRequestViewDto> findAll() {
        log.info("Find All ItemRequests");
        List<ItemRequestViewDto> itemRequestViewDtoList = itemRequestsRepository.findAllOrderByCreateDate();
        for (ItemRequestViewDto requestViewDto : itemRequestViewDtoList) {
            addResponse(requestViewDto);
        }
        return itemRequestViewDtoList;
    }

    private ItemRequestViewDto addResponse(ItemRequestViewDto itemRequestViewDto) {
        log.info("Add ItemRequest with id {} all response", itemRequestViewDto.getRequestId());
        List<ResponseViewDto> responseViewDtoList = itemRepository.findAllByRequestId(itemRequestViewDto.getRequestId());
        itemRequestViewDto.setResponseViewDtoList(responseViewDtoList);
        return itemRequestViewDto;
    }
}
