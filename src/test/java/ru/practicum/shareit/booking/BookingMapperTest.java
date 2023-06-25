package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class BookingMapperTest {
    @Autowired
    BookingMapper bookingMapper;
    @Autowired
    UserMapper userMapper;
    @Autowired
    ItemMapper itemMapper;

    BookingResponseDto bookingResponseDto;
    Booking booking;
    BookingInfoDto bookingInfoDto;

    BookingRequestDto bookingRequestDto;

    @BeforeEach
    void setUp() {
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(1);

        User itemOwner = new User();
        itemOwner.setId(1L);
        itemOwner.setName("userName");
        itemOwner.setEmail("userEmail@yandex.ru");

        User booker = new User();
        booker.setId(2L);
        booker.setName("bookerName");
        booker.setEmail("bookerEmail@yandex.ru");

        Item item = new Item();
        item.setName("itemName");
        item.setDescription("itemDescription");
        item.setAvailable(true);
        item.setOwner(itemOwner);

        booking = new Booking();
        booking.setId(1L);
        booking.setStatus(Status.APPROVED);
        booking.setStart(start);
        booking.setEnd(end);
        booking.setBooker(booker);
        booking.setItem(item);


        bookingResponseDto = new BookingResponseDto();
        bookingResponseDto.setId(1L);
        bookingResponseDto.setItem(itemMapper.toItemDto(item));
        bookingResponseDto.setStatus(Status.APPROVED);
        bookingResponseDto.setBooker(userMapper.toUserDto(booker));
        bookingResponseDto.setStart(start);
        bookingResponseDto.setEnd(end);

        bookingInfoDto = new BookingInfoDto();
        bookingInfoDto.setId(1L);
        bookingInfoDto.setStart(start);
        bookingInfoDto.setEnd(end);
        bookingInfoDto.setBookerId(booker.getId());

        bookingRequestDto = new BookingRequestDto();
        bookingRequestDto.setItemId(item.getId());
        bookingRequestDto.setStart(start);
        bookingRequestDto.setEnd(end);
    }

    @Test
    void toBookingResponse() {
        BookingResponseDto result = bookingMapper.toBookingResponse(booking);

        assertEquals(bookingResponseDto, result);
    }

    @Test
    void toBooking() {
        Booking result = bookingMapper.toBooking(bookingRequestDto);


        assertEquals(booking.getStart(), result.getStart());
        assertEquals(booking.getEnd(), result.getEnd());
    }

    @Test
    void toBookingInfo() {
        BookingInfoDto result = bookingMapper.toBookingInfo(booking);

        assertEquals(bookingInfoDto, result);
    }

}
