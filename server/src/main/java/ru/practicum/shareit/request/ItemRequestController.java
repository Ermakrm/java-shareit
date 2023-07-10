package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;


@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ItemRequestController {

    static final String USER_ID = "X-Sharer-User-Id";
    RequestMapper requestMapper;
    ItemRequestService service;

    @PostMapping
    public ItemRequestResponseDto addRequest(@RequestBody ItemRequestRequestDto itemRequestRequestDto,
                                             @RequestHeader(USER_ID) Long userId) {
        return requestMapper.toItemRequestResponseDto(service.save(itemRequestRequestDto, userId));
    }


    @GetMapping
    public List<ItemRequestResponseDto> findByUserId(@RequestHeader(USER_ID) Long userId) {
        return service.findByUserId(userId);
    }


    @GetMapping("/all")
    public List<ItemRequestResponseDto> findAllWithPagination(
            @RequestHeader(USER_ID) Long userId,
            @RequestParam(required = false, defaultValue = "0") int from,
            @RequestParam(required = false, defaultValue = "20") int size) {

        return service.findAllWithPagination(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestResponseDto findById(
            @RequestHeader(USER_ID) Long userId,
            @PathVariable Long requestId) {
        return service.findById(requestId, userId);
    }

}




