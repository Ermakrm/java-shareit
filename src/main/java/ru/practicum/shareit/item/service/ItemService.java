package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Service
public interface ItemService {

    Item create(ItemDto itemDto, Long userId);

    Item update(ItemDto itemDto, Long itemId, Long userId);

    Item findById(Long itemId);

    List<Item> search(String text);

    ItemResponseDto findByIdWithBookings(Long itemId, Long userId);

    List<ItemResponseDto> findAllByOwnerId(Long ownerId);

    Comment addComment(Long userId, Long itemId, Comment comment);
}
