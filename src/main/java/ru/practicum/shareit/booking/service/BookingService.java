package ru.practicum.shareit.booking.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

@Service
public interface BookingService {
    Booking create(Booking booking, Long bookerId);

    Booking approve(Long bookingId, Long ownerId, Boolean approved);

    Booking findByIdAndUserId(Long bookingId, Long userId);

    Booking findById(Long bookingId);

    List<Booking> findByUserIdAndState(Long userId, String state);

    List<Booking> findByOwnerIdAndState(Long ownerId, String state);

    BookingInfoDto findLastBookingByItemId(Long itemId);

    BookingInfoDto findNextBookingByItemId(Long itemId);

    Boolean hasUserBookedItem(Long userId, Long itemId);
}
