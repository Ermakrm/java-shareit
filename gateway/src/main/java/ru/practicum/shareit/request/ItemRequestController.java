package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;


@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Validated
public class ItemRequestController {

    static final String USER_ID = "X-Sharer-User-Id";
    RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> addRequest(@Valid @RequestBody ItemRequestRequestDto itemRequestRequestDto,
                                             @RequestHeader(USER_ID) Long userId) {
        return requestClient.addRequest(itemRequestRequestDto, userId);
    }


    @GetMapping
    public ResponseEntity<Object> findByUserId(@RequestHeader(USER_ID) Long userId) {
        return requestClient.findByUserId(userId);
    }


    @GetMapping("/all")
    public ResponseEntity<Object> findAllWithPagination(
            @RequestHeader(USER_ID) Long userId,
            @PositiveOrZero @RequestParam(required = false, defaultValue = "0") int from,
            @Positive @RequestParam(required = false, defaultValue = "20") int size) {

        return requestClient.findAllWithPagination(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> findById(
            @RequestHeader(USER_ID) Long userId,
            @PathVariable Long requestId) {

        return requestClient.findById(userId, requestId);
    }
}




