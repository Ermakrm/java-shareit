package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.comment.CommentResponseDto;
import ru.practicum.shareit.item.exception.IllegalCommentException;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.exception.WrongOwnerException;
import ru.practicum.shareit.item.mapper.CommentListMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemMapper itemMapper;
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final BookingService bookingService;
    private final CommentRepository commentRepository;
    private final CommentListMapper commentListMapper;

    @Override
    public Item create(ItemDto itemDto, Long userId) {
        User user = userService.findById(userId);
        Item item = itemMapper.toItem(itemDto);
        item.setOwner(user);
        return itemRepository.save(item);
    }

    @Override
    public Item update(ItemDto itemDto, Long itemId, Long userId) {
        Item item = findById(itemId);
        itemMapper.updateItemFromDto(itemDto, item);
        if (!Objects.equals(userId, item.getOwner().getId())) {
            throw new WrongOwnerException("Wrong owner");
        }
        return itemRepository.save(item);
    }

    @Override
    public Item findById(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Item not found"));
    }

    @Override
    public List<Item> search(String text) {
        return text.isBlank() ? Collections.emptyList() : itemRepository.search(text);
    }

    @Override
    public ItemResponseDto findByIdWithBookings(Long itemId, Long userId) {
        Item item = findById(itemId);
        ItemResponseDto result = addBookingsToItem(item);

        if (item.getOwner().getId().equals(userId)) {
            return result;
        } else {
            result.setLastBooking(null);
            result.setNextBooking(null);
        }

        return result;
    }

    @Override
    public List<ItemResponseDto> findAllByOwnerId(Long ownerId) {
        return itemRepository.findAllByOwnerIdOrderById(ownerId).stream()
                .map(this::addBookingsToItem)
                .collect(Collectors.toList());
    }

    @Override
    public Comment addComment(Long userId, Long itemId, Comment comment) {
        User user = userService.findById(userId);
        Item item = findById(itemId);

        if (!bookingService.hasUserBookedItem(userId, itemId)) {
            throw new IllegalCommentException(String.format("User with id %d has never booked item with id %d ",
                    userId, itemId));
        }

        comment.setAuthor(user);
        comment.setItem(item);
        comment.setCreated(LocalDateTime.now());

        return commentRepository.save(comment);
    }

    private ItemResponseDto addBookingsToItem(Item item) {
        ItemResponseDto dto = itemMapper.toItemResponse(item);
        dto.setLastBooking(bookingService.findLastBookingByItemId(item.getId()));
        dto.setNextBooking(bookingService.findNextBookingByItemId(item.getId()));
        List<CommentResponseDto> comments = commentListMapper.toCommentResponseList(
                commentRepository.findAllByItem_Id(item.getId())
        );

        if (comments.isEmpty()) {
            dto.setComments(Collections.emptyList());
        } else {
            dto.setComments(comments);
        }
        return dto;
    }
}
