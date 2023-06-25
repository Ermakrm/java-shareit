package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.exception.BookingNotFoundException;
import ru.practicum.shareit.booking.exception.BookingWrongStateRequestedException;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.item.exception.ItemNotAvailableException;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

//@ExtendWith(MockitoExtension.class)
@SpringBootTest
@FieldDefaults(level = AccessLevel.PRIVATE)
class BookingServiceImplTest {
    final Long userId = 1L;
    final Long itemId = 1L;
    @Mock
    BookingRepository bookingRepository;
    @Mock
    UserService userService;
    @Mock
    ItemService itemService;
    @Mock
    BookingMapper bookingMapper;
    @InjectMocks
    BookingServiceImpl service;
    User user1;
    User user2;
    Item item1;
    Booking booking1;
    Booking booking2;

    @BeforeEach
    void initialize() {
        LocalDateTime now = LocalDateTime.now();

        user1 = new User();
        user1.setId(userId);
        user1.setName("firstUserName");
        user1.setEmail("firstEmail@yandex.ru");

        user2 = new User();
        user2.setId(2L);
        user2.setName("secondUserName");
        user2.setEmail("secondEmail@mail.ru");

        item1 = new Item();
        item1.setId(itemId);
        item1.setName("firstItemName");
        item1.setDescription("firstItemDescription");
        item1.setAvailable(true);
        item1.setOwner(user2);

        booking1 = new Booking();
        booking1.setId(1L);
        booking1.setStart(now);
        booking1.setEnd(now.plusDays(1));
        booking1.setItem(item1);

        booking2 = new Booking();
        booking2.setId(1L);
        booking2.setStart(now);
        booking2.setEnd(now.plusDays(1));
        booking2.setStatus(Status.WAITING);
        booking2.setItem(item1);
        booking2.setBooker(user1);
    }

    @Test
    void create_whenAllValid_thenReturnBooking() {
        Booking expectedBooking = booking2;

        when(userService.findById(userId)).thenReturn(user1);
        when(itemService.findById(itemId)).thenReturn(item1);
        when(bookingRepository.save(booking2)).thenReturn(booking2);

        Booking result = service.create(booking1, userId);

        verify(userService, times(1)).findById(userId);
        verify(itemService, times(1)).findById(itemId);
        verify(bookingRepository, times(1)).save(booking2);

        assertEquals(expectedBooking, result);
    }

    @Test
    void create_whenOwnerTryToBook_thenItemNotFoundThrown() {
        when(userService.findById(userId)).thenReturn(user2);
        when(itemService.findById(itemId)).thenReturn(item1);

        ItemNotFoundException e = assertThrows(ItemNotFoundException.class,
                () -> service.create(booking1, userId));

        assertEquals("Owner can't book the item he owns", e.getMessage());
    }

    @Test
    void create_whenItemAvailable_thenItemNotFoundThrown() {
        item1.setAvailable(false);

        when(userService.findById(userId)).thenReturn(user1);
        when(itemService.findById(itemId)).thenReturn(item1);

        ItemNotAvailableException e = assertThrows(ItemNotAvailableException.class,
                () -> service.create(booking1, userId));

        assertEquals("Item with id 1 is not available", e.getMessage());
    }

    @Test
    void approve_whenAllValid_thenApprovedBooking() {
        Booking expectedBooking = booking1;
        expectedBooking.setStatus(Status.APPROVED);
        expectedBooking.setBooker(user1);

        when(itemService.findById(itemId)).thenReturn(item1);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking2));
        when(bookingRepository.save(expectedBooking)).thenReturn(expectedBooking);

        Booking result = service.approve(1L, item1.getOwner().getId(), true);

        verify(itemService, times(1)).findById(itemId);
        verify(bookingRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).save(expectedBooking);

        assertEquals(expectedBooking, result);

    }

    @Test
    void approve_whenAllValid_thenRejectedBooking() {
        Booking expectedBooking = booking1;
        expectedBooking.setStatus(Status.REJECTED);
        expectedBooking.setBooker(user1);

        when(itemService.findById(itemId)).thenReturn(item1);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking2));
        when(bookingRepository.save(expectedBooking)).thenReturn(expectedBooking);

        Booking result = service.approve(1L, item1.getOwner().getId(), false);

        verify(itemService, times(1)).findById(itemId);
        verify(bookingRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).save(expectedBooking);

        assertEquals(expectedBooking, result);
    }

    @Test
    void approve_whenStatusNotWaiting_thenItemNotAvailableThrown() {
        booking2.setStatus(Status.REJECTED);

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking2));

        ItemNotAvailableException e = assertThrows(ItemNotAvailableException.class,
                () -> service.approve(1L, item1.getOwner().getId(), false));

        assertEquals("The booking is not in WAITING status", e.getMessage());
    }

    @Test
    void approve_whenNotOwnerTryToSetStatus_thenItemNotAvailableThrown() {
        when(itemService.findById(itemId)).thenReturn(item1);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking2));

        ItemNotFoundException e = assertThrows(ItemNotFoundException.class,
                () -> service.approve(1L, 10L, false));

        assertEquals("Item with id 1 does not belong to user with id 10", e.getMessage());
    }

    @Test
    void findByIdAndUserId_whenInvokedByBooker_thenReturnedBooking() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking2));

        Booking result = service.findByIdAndUserId(booking2.getId(), booking2.getBooker().getId());

        verify(bookingRepository, times(1)).findById(anyLong());

        assertEquals(booking2, result);
    }

    @Test
    void findByIdAndUserId_whenInvokedByOwner_thenReturnedBooking() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking2));
        when(itemService.findById(booking2.getItem().getId())).thenReturn(booking2.getItem());


        Booking result = service.findByIdAndUserId(booking2.getId(), item1.getOwner().getId());

        verify(bookingRepository, times(1)).findById(anyLong());
        verify(itemService, times(1)).findById(anyLong());

        assertEquals(booking2, result);
    }

    @Test
    void findByIdAndUserId_whenInvokedOtherUser_thenBookingNotFoundThrown() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking2));
        when(itemService.findById(anyLong())).thenReturn(booking2.getItem());

        BookingNotFoundException e = assertThrows(BookingNotFoundException.class,
                () -> service.findByIdAndUserId(booking2.getId(), 10L));

        assertEquals("Booking with id 1 not found for user with id 10", e.getMessage());
    }

    @Test
    void findById_whenBookingFound_thenReturnedBooking() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking2));

        Booking result = service.findById(1L);

        verify(bookingRepository, times(1)).findById(anyLong());

        assertEquals(booking2, result);
    }

    @Test
    void findById_whenBookingNotFound_thenBookingNotFoundThrown() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());

        BookingNotFoundException e = assertThrows(BookingNotFoundException.class,
                () -> service.findById(1L));

        assertEquals("Booking with id 1 not found", e.getMessage());
    }

    @Test
    void findByUserIdAndState_whenUnknownState_thenReturnBookingWrongStateRequestedThrown() {
        when(userService.findById(userId)).thenReturn(user1);

        BookingWrongStateRequestedException e = assertThrows(BookingWrongStateRequestedException.class,
                () -> service.findByUserIdAndState(userId, "state", 0, 10));
        assertEquals("Unknown state: UNSUPPORTED_STATUS", e.getMessage());
    }

    @Test
    void findByUserIdAndState_whenCaseAll_thenInvokedFindByBookerIdOrderByStartDescMethod() {
        when(userService.findById(userId)).thenReturn(user1);

        service.findByUserIdAndState(userId, "ALL", 0, 10);

        verify(bookingRepository, times(1))
                .findByBookerIdOrderByStartDesc(anyLong(), any(Pageable.class));
    }

    @Test
    void findByUserIdAndState_whenCaseFuture_thenInvokedFindByBookerIdAndStartIsAfterOrderByStartDescMethod() {
        when(userService.findById(userId)).thenReturn(user1);

        service.findByUserIdAndState(userId, "FUTURE", 0, 10);

        verify(bookingRepository, times(1))
                .findByBookerIdAndStartIsAfterOrderByStartDesc(anyLong(), any(LocalDateTime.class), any(Pageable.class));
    }

    @Test
    void findByUserIdAndState_whenCasePast_thenInvokedFindByBookerIdAndEndIsBeforeOrderByStartDescMethod() {
        when(userService.findById(userId)).thenReturn(user1);

        service.findByUserIdAndState(userId, "PAST", 0, 10);

        verify(bookingRepository, times(1))
                .findByBookerIdAndEndIsBeforeOrderByStartDesc(anyLong(), any(LocalDateTime.class), any(Pageable.class));
    }

    @Test
    void findByUserIdAndState_whenCasePast_thenInvokedFindAllByBookerIdAndStartBeforeAndEndAfterOrderByIdAscMethod() {
        when(userService.findById(userId)).thenReturn(user1);

        service.findByUserIdAndState(userId, "CURRENT", 0, 10);

        verify(bookingRepository, times(1))
                .findAllByBookerIdAndStartBeforeAndEndAfterOrderByIdAsc(
                        anyLong(), any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class));
    }

    @Test
    void findByUserIdAndState_whenCaseWaiting_thenInvokedFindByBookerIdAndStatusOrderByStartDescMethod() {
        when(userService.findById(userId)).thenReturn(user1);

        service.findByUserIdAndState(userId, "WAITING", 0, 10);

        verify(bookingRepository, times(1))
                .findByBookerIdAndStatusOrderByStartDesc(
                        anyLong(), any(Status.class), any(Pageable.class));
    }

    @Test
    void findByUserIdAndState_whenCaseRejected_thenInvokedFindByBookerIdAndStatusOrderByStartDescMethod() {
        when(userService.findById(userId)).thenReturn(user1);

        service.findByUserIdAndState(userId, "REJECTED", 0, 10);

        verify(bookingRepository, times(1))
                .findByBookerIdAndStatusOrderByStartDesc(
                        anyLong(), any(Status.class), any(Pageable.class));
    }

    @Test
    void findByOwnerIdAndState_whenUnknownState_thenReturnBookingWrongStateRequestedThrown() {
        when(userService.findById(userId)).thenReturn(user1);

        BookingWrongStateRequestedException e = assertThrows(BookingWrongStateRequestedException.class,
                () -> service.findByOwnerIdAndState(userId, "state", 0, 10));
        assertEquals("Unknown state: UNSUPPORTED_STATUS", e.getMessage());
    }

    @Test
    void findByOwnerIdAndState_whenCaseAll_thenInvokedFindByOwnerIdMethod() {
        when(userService.findById(userId)).thenReturn(user1);

        service.findByOwnerIdAndState(userId, "ALL", 0, 10);

        verify(bookingRepository, times(1))
                .findByOwnerId(any(User.class), any(Pageable.class));
    }

    @Test
    void findByOwnerIdAndState_whenCaseFuture_thenInvokedFindByOwnerIdInFutureMethod() {
        when(userService.findById(userId)).thenReturn(user1);

        service.findByOwnerIdAndState(userId, "FUTURE", 0, 10);

        verify(bookingRepository, times(1))
                .findByOwnerIdInFuture(any(User.class), any(Pageable.class));
    }

    @Test
    void findByOwnerIdAndState_whenCasePast_thenInvokedFindByOwnerIdInPastMethod() {
        when(userService.findById(userId)).thenReturn(user1);

        service.findByOwnerIdAndState(userId, "PAST", 0, 10);

        verify(bookingRepository, times(1))
                .findByOwnerIdInPast(any(User.class), any(Pageable.class));
    }

    @Test
    void findByOwnerIdAndState_whenCaseCurrent_thenInvokedFindByOwnerIdInCurrentMethod() {
        when(userService.findById(userId)).thenReturn(user1);

        service.findByOwnerIdAndState(userId, "CURRENT", 0, 10);

        verify(bookingRepository, times(1))
                .findByOwnerIdInCurrent(any(User.class), any(Pageable.class));
    }

    @Test
    void findByOwnerIdAndState_whenCaseWaiting_thenInvokedFindByOwnerIdAndStatusMethod() {
        when(userService.findById(userId)).thenReturn(user1);

        service.findByOwnerIdAndState(userId, "WAITING", 0, 10);

        verify(bookingRepository, times(1))
                .findByOwnerIdAndStatus(any(User.class), any(Status.class), any(Pageable.class));
    }

    @Test
    void findByOwnerIdAndState_whenCaseRejected_thenInvokedFindByOwnerIdAndStatusMethod() {
        when(userService.findById(userId)).thenReturn(user1);

        service.findByOwnerIdAndState(userId, "REJECTED", 0, 10);

        verify(bookingRepository, times(1))
                .findByOwnerIdAndStatus(any(User.class), any(Status.class), any(Pageable.class));
    }

    @Test
    void findLastBookingByItemId() {
        service.findLastBookingByItemId(itemId);

        verify(bookingMapper, times(1)).toBookingInfo(any());
        verify(bookingRepository, times(1)).findLastBookingByItemId(anyLong());
    }

    @Test
    void findNextBookingByItemId() {
        service.findNextBookingByItemId(itemId);

        verify(bookingMapper, times(1)).toBookingInfo(any());
        verify(bookingRepository, times(1)).findNextBookingByItemId(anyLong());
    }

    @Test
    void hasUserBookedItem() {
        service.hasUserBookedItem(userId, itemId);

        verify(bookingRepository, times(1))
                .findByBookerIdAndItemIdAndEndIsBeforeAndStatusOrderByStartDesc(
                        anyLong(), anyLong(), any(LocalDateTime.class), any(Status.class)
                );
    }
}