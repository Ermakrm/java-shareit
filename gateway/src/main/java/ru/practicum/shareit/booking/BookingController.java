package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.exception.BookingWrongStateRequestedException;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Validated
public class BookingController {

    static final String USER_ID = "X-Sharer-User-Id";
    BookingClient bookingClient;


    @PostMapping
    public ResponseEntity<Object> create(
            @Valid @RequestBody BookingRequestDto bookingRequestDto,
            @RequestHeader(USER_ID) @NotNull Long bookerId) {
        return bookingClient.create(bookingRequestDto, bookerId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approve(
            @RequestParam Boolean approved,
            @PathVariable Long bookingId,
            @RequestHeader(USER_ID) @NotNull Long ownerId) {
        return bookingClient.approve(approved, bookingId, ownerId);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> findById(
            @PathVariable Long bookingId,
            @RequestHeader(USER_ID) @NotNull Long userId) {
        return bookingClient.findById(bookingId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> findAllByUserIdAndState(
            @RequestHeader(USER_ID) @NotNull Long userId,
            @RequestParam(defaultValue = "ALL") String stateParam,
            @PositiveOrZero @RequestParam(required = false, defaultValue = "0") int from,
            @Positive @RequestParam(required = false, defaultValue = "20") int size) {

        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new BookingWrongStateRequestedException("Unknown state: UNSUPPORTED_STATUS"));
        return bookingClient.findAllByUserIdAndState(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> findAllByOwnerIdAndState(
            @RequestHeader(USER_ID) @NotNull Long ownerId,
            @RequestParam(defaultValue = "ALL") String stateParam,
            @PositiveOrZero @RequestParam(required = false, defaultValue = "0") int from,
            @Positive @RequestParam(required = false, defaultValue = "20") int size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new BookingWrongStateRequestedException("Unknown state: UNSUPPORTED_STATUS"));
        return bookingClient.findAllByOwnerIdAndState(ownerId, state, from, size);
    }
}