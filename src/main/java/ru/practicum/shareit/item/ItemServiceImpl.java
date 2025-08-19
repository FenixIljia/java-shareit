package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingViewDTO;
import ru.practicum.shareit.exception.AccessRightException;
import ru.practicum.shareit.exception.NotCompletedBooking;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.CommentMapperDto;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;

    @Override
    public void delete(long id, long userId) {
        log.info("Delete item with id={}", id);
        checkAccessRight(id, userId);
        Optional<Item> item = itemRepository.findById((int) id);
        item.ifPresent(itemRepository::delete);
    }

    @Override
    public List<ItemViewOwner> findAllByUserId(long user_id) {
        log.info("Find all items by user with id={}", user_id);
        List<ItemViewOwner> itemViewOwners = itemRepository.findAllByUserId(user_id);
        for (ItemViewOwner itemViewOwner : itemViewOwners) {
            addCommentsInListItemView(itemViewOwner);
            addBookingInListItemView(itemViewOwner);
        }
        return itemViewOwners;
    }

    @Override
    public ItemViewOwner findById(long id, long userId) {
        log.info("Find item with id {}", id);
        Optional<ItemViewOwner> item = itemRepository.findByItemId(id);
        if (item.isEmpty()) {
            throw new NotFoundException("Item with id " + id + " not found");
        }
        ItemViewOwner itemViewOwner = item.get();
        addCommentsInListItemView(itemViewOwner);
        if (itemViewOwner.getOwnerId() == userId) {
            addBookingInListItemView(itemViewOwner);
        }
        return itemViewOwner;
    }

    @Override
    public List<ItemView> findAllByName(String name) {
        if (name.isBlank()) {
            log.warn("Name is blank");
            return List.of();
        }
        log.info("Find items by name {}", name);
        return itemRepository.findAllAvailableTrue(name);
    }

    @Override
    public Item save(ItemDto itemDto, long userId) {
        log.info("Save item {}", itemDto);
        if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException("User with id " + userId + " not found");
        }
        Item item = Item.builder()
                .user(userRepository.findById(userId).get())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .build();
        return itemRepository.save(item);
    }

    @Override
    public Item update(ItemUpdate newItem, long id, Long userId) {
        log.info("Update item with id {}", id);
        Optional<Item> itemOptional = itemRepository.findById((int) id);
            checkAccessRight(id, userId);
        if (itemOptional.isEmpty()) {
            throw new NotFoundException("Item with id " + id + " not found");
        }
        Item item = itemOptional.get();
        if (newItem.getName() != null) {
            item.setName(newItem.getName());
        }
        if (newItem.getDescription() != null) {
            item.setDescription(newItem.getDescription());
        }
        if (newItem.getAvailable() != null) {
            item.setAvailable(newItem.getAvailable());
        }
        return itemRepository.save(item);
    }

    @Override
    public CommentView save(SaveComment saveComment, long itemId, Long userId) {
        log.info("Save comment {} for item {}", saveComment, itemId);
        validateUserRentedItem(userId, itemId);
        if (!hasUserCompletedBookingForItem(userId, itemId)) {
            throw new NotCompletedBooking("User with id " + userId + " not has been rented for item with id " + itemId);
        }
        Comment newComment = Comment.builder()
                .text(saveComment.getText())
                .user(userRepository.findById(userId).get())
                .item(itemRepository.findById((int) itemId).get())
                .build();
        return CommentMapperDto.toViewDto(commentRepository.save(newComment));
    }

    private void checkAccessRight(long id, long userId) {
        Optional<Item> item = itemRepository.findById((int) id);
        if (item.isPresent()) {
            if (item.get().getUser().getId() != userId) {
                throw new AccessRightException(String.format(
                        "Access rights denied. User %d is not the owner of item %d",
                        userId,
                        id)
                );
            }
        }
    }

    private void validateUserRentedItem(Long userId, Long itemId) {
        if (bookingRepository.findAllByUserIdAndItemItemId(userId, itemId).isEmpty()) {
            throw new AccessRightException("User with id " + userId + " not booked item with id " + itemId);
        }
    }

    private void addCommentsInListItemView(ItemViewOwner itemViewOwner) {
        itemViewOwner.setComments(commentRepository.findAllByItemId(itemViewOwner.getItemId()));
    }

    private void addBookingInListItemView(ItemViewOwner itemViewOwner) {
        List<BookingViewDTO> bookingViewDTOS = bookingRepository.findAllByItem_ItemId(itemViewOwner.getItemId());
        for (BookingViewDTO bookingViewDTO : bookingViewDTOS) {
            if (bookingViewDTO.getEndDate().isBefore(LocalDateTime.now())) {
                if (itemViewOwner.getLastBooking() != null) {
                    if (itemViewOwner.getLastBooking().getEndDate().isBefore(bookingViewDTO.getStartDate())) {
                        itemViewOwner.setLastBooking(bookingViewDTO);
                    }
                } else {
                    itemViewOwner.setLastBooking(bookingViewDTO);
                }
            } else {
                if (itemViewOwner.getNextBooking() == null) {
                    itemViewOwner.setNextBooking(bookingViewDTO);
                } else {
                    if (itemViewOwner.getNextBooking().getStartDate().isAfter(bookingViewDTO.getEndDate())) {
                        itemViewOwner.setNextBooking(bookingViewDTO);
                    }
                }

            }
        }
    }

    private Boolean hasUserCompletedBookingForItem(Long userId, Long itemId) {
        List<BookingViewDTO> bookingViewDTOs = bookingRepository.findAllByUserIdAndItemItemId(userId, itemId);
        for (BookingViewDTO bookingViewDTO : bookingViewDTOs) {
            if (bookingViewDTO.getEndDate().isBefore(LocalDateTime.now()) || bookingViewDTO.getEndDate().equals(LocalDateTime.now())) {
                return true;
            }
        }
        return false;
    }
}
