package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemStorage;

import java.util.ArrayList;
import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;

    @Autowired
    public ItemServiceImpl(ItemStorage itemStorage) {
        this.itemStorage = itemStorage;
    }

    @Override
    public ItemDto createItem(ItemDto itemDto, Long userId) {
        return ItemMapper.toItemDto(itemStorage.createItem(ItemMapper.toItem(itemDto), userId));
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, Long itemId, Long userId) {
        itemDto.setId(itemId);
        return ItemMapper.toItemDto(itemStorage.updateItem(ItemMapper.toItem(itemDto), userId));
    }

    @Override
    public ItemDto getItem(Long itemId) {
        return ItemMapper.toItemDto(itemStorage.getItem(itemId));
    }

    @Override
    public List<ItemDto> getAllItems(Long userId) {
        return toItemDtoList(itemStorage.getAllItems(userId));
    }

    @Override
    public List<ItemDto> search(String text) {
        return toItemDtoList(itemStorage.search(text));
    }

    private List<ItemDto> toItemDtoList(List<Item> items) {
        List<ItemDto> itemsDto = new ArrayList<>();
        for (Item item : items) {
            itemsDto.add(ItemMapper.toItemDto(item));
        }
        return itemsDto;
    }
}
