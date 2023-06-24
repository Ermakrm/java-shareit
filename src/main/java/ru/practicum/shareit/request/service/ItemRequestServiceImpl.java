package ru.practicum.shareit.request.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.exception.RequestNotFoundException;
import ru.practicum.shareit.request.mapper.RequestListMapper;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ItemRequestServiceImpl implements ItemRequestService {

    ItemRequestRepository itemRequestRepository;
    UserService userService;
    RequestMapper requestMapper;
    ItemService itemService;
    RequestListMapper requestListMapper;

    @Override
    public ItemRequest save(ItemRequestRequestDto itemRequestRequestDto, Long userId) {
        User user = userService.findById(userId);
        ItemRequest itemRequest = requestMapper.toItemRequest(itemRequestRequestDto);
        itemRequest.setRequester(user);
        itemRequest.setCreated(LocalDateTime.now());
        return itemRequestRepository.save(itemRequest);
    }

    public List<ItemRequestResponseDto> findByUserId(Long userId) {
        userService.findById(userId);
        List<ItemRequestResponseDto> requests = requestListMapper.toResponseDtoList(
                itemRequestRepository.findAllByRequesterId(userId)
        );
        addItemsResponseDto(requests);
        return requests;
    }

    @Override
    public List<ItemRequestResponseDto> findAllWithPagination(Long userId, int from, int size) {
        userService.findById(userId);
        int page = from / size;

        List<ItemRequestResponseDto> requests = requestListMapper.toResponseDtoList(
                itemRequestRepository.findAllByRequesterIdNot(userId, PageRequest.of(page, size))
        );
        addItemsResponseDto(requests);
        return requests;
    }

    @Override
    public ItemRequestResponseDto findById(Long requestId, Long userId) {
        userService.findById(userId);

        ItemRequestResponseDto itemRequest = requestMapper.toItemRequestResponseDto(
                itemRequestRepository.findById(requestId).orElseThrow(
                        () -> new RequestNotFoundException(String.format("Request with id %d not found", requestId))
                )
        );
        addItemsResponseDto(itemRequest);
        return itemRequest;
    }

    @Override
    public ItemRequest getItemRequestById(Long requestId) {
        return itemRequestRepository.findById(requestId).orElseThrow(
                () -> new RequestNotFoundException(String.format("Request with id %d not found", requestId))
        );
    }

    private void addItemsResponseDto(List<ItemRequestResponseDto> request) {
        request.forEach(this::addItemsResponseDto);
    }

    private void addItemsResponseDto(ItemRequestResponseDto request) {
        request.setItems(itemService.findAllByRequestId(request.getId()));
    }
}
