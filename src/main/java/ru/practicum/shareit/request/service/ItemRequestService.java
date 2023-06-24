package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestService {
    ItemRequest save(ItemRequestRequestDto itemRequestRequestDto, Long userId);

    List<ItemRequestResponseDto> findByUserId(Long userId);

    List<ItemRequestResponseDto> findAllWithPagination(Long userId, int from, int size);

    ItemRequestResponseDto findById(Long requestId, Long userId);

    ItemRequest getItemRequestById(Long requestId);
}
