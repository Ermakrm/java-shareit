package ru.practicum.shareit.booking.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.booking.exception.BookingNotFoundException;
import ru.practicum.shareit.booking.exception.BookingWrongStateRequestedException;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.exception.ItemNotAvailableException;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookingServiceImpl implements BookingService {

    BookingRepository bookingRepository;
    UserService userService;
    ItemService itemService;
    BookingMapper bookingMapper;

    public BookingServiceImpl(BookingRepository bookingRepository, UserService userService,
                              @Lazy ItemService itemService, BookingMapper bookingMapper) {
        this.bookingRepository = bookingRepository;
        this.userService = userService;
        this.itemService = itemService;
        this.bookingMapper = bookingMapper;
    }

    @Override
    public Booking create(Booking booking, Long bookerId) {
        User user = userService.findById(bookerId);
        Item item = itemService.findById(booking.getItem().getId());

        if (Objects.equals(item.getOwner().getId(), user.getId())) {
            throw new ItemNotFoundException("Owner can't book the item he owns");
        }

        if (!item.getAvailable()) {
            throw new ItemNotAvailableException(String.format("Item with id %d is not available", item.getId()));
        }

        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(Status.WAITING);
        return bookingRepository.save(booking);
    }

    @Override
    public Booking approve(Long bookingId, Long ownerId, Boolean approved) {
        checkUserExists(ownerId);

        Booking booking = findById(bookingId);
        if (!booking.getStatus().equals(Status.WAITING)) {
            throw new ItemNotAvailableException("The booking is not in WAITING status");
        }

        Long itemId = booking.getItem().getId();
        Item item = itemService.findById(itemId);

        Long storedOwnerId = item.getOwner().getId();
        if (!storedOwnerId.equals(ownerId)) {
            throw new ItemNotFoundException(String.format("Item with id %d does not belong to user with id %d", itemId,
                    ownerId));
        }

        if (approved) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }

        return bookingRepository.save(booking);
    }

    @Override
    public Booking findByIdAndUserId(Long bookingId, Long userId) {
        Booking booking = findById(bookingId);

        if (booking.getBooker().getId().equals(userId)) {
            // Returns booking only for the booker ...
            return booking;
        }

        Item item = itemService.findById(booking.getItem().getId());
        if (item.getOwner().getId().equals(userId)) {
            // ... or for the owner
            return booking;
        }

        throw new BookingNotFoundException(String.format("Booking with id %d not found for user with id %d", bookingId,
                userId));
    }

    @Override
    public Booking findById(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(
                        String.format("Booking with id %d not found", bookingId))
                );
    }

    @Override
    public List<Booking> findByUserIdAndState(Long userId, String state) {
        checkUserExists(userId);

        State requestBookingState = checkState(state);

        List<Booking> result = new ArrayList<>();
        switch (requestBookingState) {
            case ALL:
                result = bookingRepository.findByBookerIdOrderByStartDesc(userId);
                break;

            case FUTURE:
                result = bookingRepository.findByBookerIdAndStartIsAfterOrderByStartDesc(userId, LocalDateTime.now());
                break;

            case PAST:
                result = bookingRepository.findByBookerIdAndEndIsBeforeOrderByStartDesc(userId, LocalDateTime.now());
                break;

            case CURRENT:
                result = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByIdAsc(userId, LocalDateTime.now(),
                        LocalDateTime.now());
                break;

            case WAITING:
            case REJECTED:
                Status bookingStatus = Status.valueOf(state);
                result = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, bookingStatus);
                break;
        }

        return result;
    }

    @Override
    public List<Booking> findByOwnerIdAndState(Long ownerId, String state) {
        User owner = userService.findById(ownerId);

        State requestBookingState = checkState(state);

        List<Booking> result = new ArrayList<>();
        switch (requestBookingState) {
            case ALL:
                result = bookingRepository.findByOwnerId(owner);
                break;

            case FUTURE:
                result = bookingRepository.findByOwnerIdInFuture(owner);
                break;

            case PAST:
                result = bookingRepository.findByOwnerIdInPast(owner);
                break;

            case CURRENT:
                result = bookingRepository.findByOwnerIdInCurrent(owner);
                break;

            case WAITING:
            case REJECTED:
                Status bookingStatus = Status.valueOf(state);

                result = bookingRepository.findByOwnerIdAndStatus(owner, bookingStatus);
                break;
        }

        return result;
    }

    @Override
    public BookingInfoDto findLastBookingByItemId(Long itemId) {
        return bookingMapper.toBookingInfo(bookingRepository.findLastBookingByItemId(itemId));
    }

    @Override
    public BookingInfoDto findNextBookingByItemId(Long itemId) {
        return bookingMapper.toBookingInfo(bookingRepository.findNextBookingByItemId(itemId));
    }

    public Boolean hasUserBookedItem(Long userId, Long itemId) {
        return bookingRepository.findByBookerIdAndItemIdAndEndIsBeforeAndStatusOrderByStartDesc(userId, itemId, LocalDateTime.now(),
                Status.APPROVED).size() > 0;
    }

    private void checkUserExists(Long userId) {
        userService.findById(userId);
    }

    private State checkState(String state) {
        try {
            return State.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new BookingWrongStateRequestedException("Unknown state: UNSUPPORTED_STATUS");
        }
    }
}
