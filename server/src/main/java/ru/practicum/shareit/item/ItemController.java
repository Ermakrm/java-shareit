package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.comment.CommentRequestDto;
import ru.practicum.shareit.item.dto.comment.CommentResponseDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemListMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ItemController {
    static final String USER_ID = "X-Sharer-User-Id";
    ItemService itemService;
    ItemMapper itemMapper;
    ItemListMapper itemListMapper;
    CommentMapper commentMapper;

    @PostMapping
    public ItemDto createItem(@RequestBody ItemDto itemDto,
                              @RequestHeader(USER_ID) Long userId) {
        return itemMapper.toItemDto(itemService.create(itemDto, userId));
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestBody ItemDto itemDto,
                              @PathVariable Long itemId,
                              @RequestHeader(USER_ID) Long userId) {
        return itemMapper.toItemDto(itemService.update(itemDto, itemId, userId));
    }

    @GetMapping("/{itemId}")
    public ItemResponseDto getItem(@PathVariable Long itemId, @RequestHeader(USER_ID) Long userId) {
        return itemService.findByIdWithBookings(itemId, userId);
    }

    @GetMapping()
    public List<ItemResponseDto> getAllItems(
            @RequestHeader(USER_ID) Long userId,
            @RequestParam(required = false, defaultValue = "0") int from,
            @RequestParam(required = false, defaultValue = "20") int size) {
        return itemService.findAllByOwnerId(userId, from, size);
    }

    @GetMapping("/search")
    public List<ItemDto> search(
            @RequestParam String text,
            @RequestHeader(USER_ID) Long userId,
            @RequestParam(required = false, defaultValue = "0") int from,
            @RequestParam(required = false, defaultValue = "20") int size) {
        return itemListMapper.toItemDtoList(itemService.search(text, from, size));
    }

    @PostMapping("/{itemId}/comment")
    public CommentResponseDto addComment(
            @RequestHeader(USER_ID) Long userId,
            @PathVariable Long itemId,
            @RequestBody CommentRequestDto commentRequestDto) {
        return commentMapper.toCommentResponseDto(itemService.addComment(userId, itemId,
                commentMapper.toComment(commentRequestDto)));
    }
}
