package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookingController {

    static final String USER_ID = "X-Sharer-User-Id";
    BookingService bookingService;
    BookingMapper bookingMapper;

    @PostMapping
    public BookingResponseDto create(
            @Valid @RequestBody BookingRequestDto bookingRequestDto,
            @RequestHeader(USER_ID) @NotNull Long bookerId) {
        return bookingMapper.toBookingResponse(bookingService.create(bookingMapper.toBooking(bookingRequestDto),
                bookerId));
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto approve(
            @RequestParam Boolean approved, @PathVariable Long bookingId,
            @RequestHeader(USER_ID) @NotNull Long ownerId) {
        return bookingMapper.toBookingResponse(bookingService.approve(bookingId, ownerId, approved));
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto findById(
            @PathVariable Long bookingId,
            @RequestHeader(USER_ID) @NotNull Long userId) {
        return bookingMapper.toBookingResponse(bookingService.findByIdAndUserId(bookingId, userId));
    }

    @GetMapping
    public Collection<BookingResponseDto> findAllByUserIdAndState(
            @RequestHeader(USER_ID) @NotNull Long userId,
            @RequestParam(defaultValue = "ALL") String state) {
        List<Booking> result = bookingService.findByUserIdAndState(userId, state);
        return result.stream().map(bookingMapper::toBookingResponse).collect(Collectors.toList());
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> findAllByOwnerIdAndState(
            @RequestHeader(USER_ID) @NotNull Long ownerId,
            @RequestParam(defaultValue = "ALL") String state) {
        List<Booking> result = bookingService.findByOwnerIdAndState(ownerId, state);
        return result.stream().map(bookingMapper::toBookingResponse).collect(Collectors.toList());
    }
}