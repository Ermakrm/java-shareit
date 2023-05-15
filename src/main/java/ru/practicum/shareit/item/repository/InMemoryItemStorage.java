package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.repository.InMemoryUserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryItemStorage implements ItemStorage {

    private static long generatorItemId = 0;
    private final Map<Long, Item> items = new HashMap<>();

    private long getNextId() {
        return ++generatorItemId;
    }

    @Override
    public Item createItem(Item item, Long userId) {
        if (!InMemoryUserStorage.isUserExist(userId)) {
            throw new IllegalArgumentException("Такого пользователя не существует!");
        }
        item.setId(getNextId());
        item.setOwner(InMemoryUserStorage.getUserById(userId));
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item updateItem(Item item, Long userId) {
        if (!items.containsKey(item.getId())) {
            throw new IllegalArgumentException("Такого предмета не существует");
        }
        Item i = items.get(item.getId());
        if (!Objects.equals(i.getOwner().getId(), userId)) {
            throw new IllegalArgumentException("Это предмет другого пользователя");
        }
        if (item.getName() != null) {
            i.setName(item.getName());
        }
        if (item.getAvailable() != null) {
            i.setAvailable(item.getAvailable());
        }
        if (item.getDescription() != null) {
            i.setDescription(item.getDescription());
        }
        items.put(i.getId(), i);
        return i;
    }

    @Override
    public Item getItem(Long itemId) {
        return items.get(itemId);
    }

    @Override
    public List<Item> getAllItems(Long userId) {
        List<Item> i = new ArrayList<>(items.values());
        return i.stream().filter(item -> Objects.equals(item.getOwner().getId(), userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> search(String text) {
        if (text.isBlank() || text.isEmpty()) {
            return List.of();
        }
        List<Item> i = new ArrayList<>(items.values());
        return i.stream().filter(item -> item.getAvailable()
                        && (item.getName().toLowerCase().contains(text.toLowerCase())
                        || item.getDescription().toLowerCase().contains(text.toLowerCase())))
                .collect(Collectors.toList());
    }
}
