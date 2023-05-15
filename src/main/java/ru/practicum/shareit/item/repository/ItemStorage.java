package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {
    Item createItem(Item item, Long userId);

    Item updateItem(Item item, Long userId);

    Item getItem(Long itemId);

    List<Item> getAllItems(Long userId);

    List<Item> search(String text);
}
