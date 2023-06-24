package ru.practicum.shareit.item.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.comment.CommentResponseDto;
import ru.practicum.shareit.item.exception.IllegalCommentException;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.exception.WrongOwnerException;
import ru.practicum.shareit.item.mapper.CommentListMapper;
import ru.practicum.shareit.item.mapper.ItemListMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ItemServiceImpl implements ItemService {

    ItemMapper itemMapper;
    ItemRepository itemRepository;
    UserService userService;
    BookingService bookingService;
    CommentRepository commentRepository;
    CommentListMapper commentListMapper;
    ItemListMapper itemListMapper;
    ItemRequestService itemRequestService;

    public ItemServiceImpl(ItemMapper itemMapper, ItemRepository itemRepository,
                           UserService userService, BookingService bookingService,
                           CommentRepository commentRepository, CommentListMapper commentListMapper,
                           ItemListMapper itemListMapper, @Lazy ItemRequestService itemRequestService) {
        this.itemMapper = itemMapper;
        this.itemRepository = itemRepository;
        this.userService = userService;
        this.bookingService = bookingService;
        this.commentRepository = commentRepository;
        this.commentListMapper = commentListMapper;
        this.itemListMapper = itemListMapper;
        this.itemRequestService = itemRequestService;
    }

    @Override
    public Item create(ItemDto itemDto, Long userId) {
        User user = userService.findById(userId);
        Item item = itemMapper.toItem(itemDto);
        item.setOwner(user);
        if (itemDto.getRequestId() != null) {
            item.setRequest(itemRequestService.getItemRequestById(itemDto.getRequestId()));
        }
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
    public List<Item> search(String text, int from, int size) {
        int page = from / size;
        return text.isBlank() ? Collections.emptyList() : itemRepository.search(text, PageRequest.of(page, size));
    }

    @Override
    public ItemResponseDto findByIdWithBookings(Long itemId, Long userId) {
        Item item = findById(itemId);
        ItemResponseDto result = addBookingsToItem(item);

        if (!Objects.equals(item.getOwner().getId(), userId)) {
            result.setLastBooking(null);
            result.setNextBooking(null);
        }

        return result;
    }

    @Override
    public List<ItemResponseDto> findAllByOwnerId(Long ownerId, int from, int size) {
        int page = from / size;
        return itemRepository.findAllByOwnerIdOrderById(ownerId, PageRequest.of(page, size)).stream()
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

    @Override
    public List<ItemResponseDto> findAllByRequestId(Long requestId) {
        return itemListMapper.toItemResponseDtoList(itemRepository.findAllByRequestId(requestId));
    }


    private ItemResponseDto addBookingsToItem(Item item) {
        ItemResponseDto dto = itemMapper.toItemResponse(item);

        dto.setLastBooking(bookingService.findLastBookingByItemId(item.getId()));
        dto.setNextBooking(bookingService.findNextBookingByItemId(item.getId()));

        List<CommentResponseDto> comments = commentListMapper.toCommentResponseList(
                commentRepository.findAllByItem_Id(item.getId())
        );

        dto.setComments(comments);
        return dto;
    }
}
