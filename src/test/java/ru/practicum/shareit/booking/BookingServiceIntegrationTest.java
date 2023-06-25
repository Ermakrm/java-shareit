package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.booking.exception.BookingNotFoundException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.exception.ItemNotAvailableException;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {"db.name=test"})
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE)
class BookingServiceIntegrationTest {
    private final LocalDateTime now = LocalDateTime.now();
    @Autowired
    BookingService bookingService;
    @Autowired
    BookingRepository bookingRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ItemRepository itemRepository;
    User user1;
    User user2;

    Item item1;
    Item item2;
    Item item3;
    Booking bookingWaiting;
    Booking bookingFuture;
    Booking bookingPast;
    Booking bookingCurrent;

    @BeforeEach
    void setUp() {
        User u = new User();
        u.setName("firstUserName");
        u.setEmail("firstEmail@yandex.ru");
        user1 = userRepository.save(u);

        User u2 = new User();
        u2.setName("secondUserName");
        u2.setEmail("secondUserEmail");
        user2 = userRepository.save(u2);

        Item i = new Item();
        i.setName("firstItemName");
        i.setDescription("alfa");
        i.setAvailable(true);
        i.setOwner(user1);
        item1 = itemRepository.save(i);

        Item i2 = new Item();
        i2.setName("secondItemName");
        i2.setDescription("beta");
        i2.setAvailable(true);
        i2.setOwner(user1);
        item2 = itemRepository.save(i2);

        Item i3 = new Item();
        i3.setName("thirdItemName");
        i3.setDescription("omega");
        i3.setAvailable(true);
        i3.setOwner(user2);
        item3 = itemRepository.save(i3);


        Booking b1 = new Booking();
        b1.setStatus(Status.APPROVED);
        b1.setStart(now.plusDays(10));
        b1.setEnd(now.plusDays(20));
        b1.setItem(item1);
        b1.setBooker(user2);
        bookingFuture = bookingRepository.save(b1);

        Booking b2 = new Booking();
        b2.setStatus(Status.APPROVED);
        b2.setStart(now.minusDays(1));
        b2.setEnd(now.plusDays(1));
        b2.setItem(item2);
        b2.setBooker(user2);
        bookingCurrent = bookingRepository.save(b2);

        Booking b3 = new Booking();
        b3.setStatus(Status.APPROVED);
        b3.setStart(now.minusDays(20));
        b3.setEnd(now.minusDays(10));
        b3.setItem(item3);
        b3.setBooker(user1);
        bookingPast = bookingRepository.save(b3);

        Booking b4 = new Booking();
        b4.setStatus(Status.WAITING);
        b4.setStart(now.plusDays(20));
        b4.setEnd(now.plusDays(30));
        b4.setItem(item3);
        b4.setBooker(user1);
        bookingWaiting = bookingRepository.save(b4);
    }

    @Test
    void create() {
        Booking b = new Booking();
        b.setStart(now.plusHours(1));
        b.setEnd(now.minusDays(1));
        b.setBooker(user2);
        b.setItem(item1);
        b.setStatus(Status.WAITING);

        Booking result = bookingService.create(b, user2.getId());
        b.setId(result.getId());

        assertEquals(b, result);
    }

    @Test
    void create_whenItemNotAvailable_thenItemNotAvailableThrown() {
        item1.setAvailable(false);
        Booking b = new Booking();
        b.setStart(now.plusHours(1));
        b.setEnd(now.minusDays(1));
        b.setBooker(user2);
        b.setItem(item1);
        b.setStatus(Status.WAITING);

        assertThrows(ItemNotAvailableException.class, () -> bookingService.create(b, user2.getId()));
    }

    @Test
    void create_whenOwnerTryToBook_thenItemNotFoundThrown() {
        Booking b = new Booking();
        b.setStart(now.plusHours(1));
        b.setEnd(now.minusDays(1));
        b.setBooker(user2);
        b.setItem(item1);
        b.setStatus(Status.WAITING);

        assertThrows(ItemNotFoundException.class, () -> bookingService.create(b, item1.getOwner().getId()));
    }

    @Test
    void approve() {
        Booking result = bookingService.approve(bookingWaiting.getId(), user2.getId(), true);

        Booking expected = bookingWaiting;
        expected.setStatus(Status.APPROVED);

        assertEquals(expected, result);
    }

    @Test
    void approve_whenStatusNotWaiting_thenItemNotAvailableThrown() {
        bookingService.approve(bookingWaiting.getId(), user2.getId(), true);
        assertThrows(ItemNotAvailableException.class,
                () -> bookingService.approve(bookingWaiting.getId(), user2.getId(), true));
    }

    @Test
    void approve_whenTryToApproveNotOwner_thenItemNotFoundThrown() {
        assertThrows(ItemNotFoundException.class,
                () -> bookingService.approve(bookingWaiting.getId(), user1.getId(), true));
    }

    @Test
    void findByIdAndUserId() {
        Booking expected = bookingWaiting;

        Booking result = bookingService.findByIdAndUserId(bookingWaiting.getId(), user2.getId());

        assertEquals(expected, result);
    }

    @Test
    void findById() {
        Booking expected = bookingWaiting;

        Booking result = bookingService.findById(bookingWaiting.getId());

        assertEquals(expected, result);
    }

    @Test
    void findById_whenNotFound_thenBookingNotFoundThrown() {
        assertThrows(BookingNotFoundException.class,
                () -> bookingService.findById(100L));
    }

    @Test
    void findByUserIdAndState_caseAll() {
        List<Booking> result = bookingService.findByUserIdAndState(user1.getId(), "ALL", 0, 5);

        assertFalse(result.isEmpty());
        assertEquals(2, result.size());
    }

    @Test
    void findByUserIdAndState_caseFuture() {
        List<Booking> result = bookingService.findByUserIdAndState(user1.getId(), "FUTURE", 0, 5);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void findByUserIdAndState_casePast() {
        List<Booking> result = bookingService.findByUserIdAndState(user1.getId(), "PAST", 0, 5);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void findByUserIdAndState_caseCurrent() {
        List<Booking> result = bookingService.findByUserIdAndState(user2.getId(), "CURRENT", 0, 5);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void findByUserIdAndState_caseWaiting() {
        List<Booking> result = bookingService.findByUserIdAndState(user1.getId(), "WAITING", 0, 5);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void findByOwnerIdAndState_caseAll() {
        List<Booking> result = bookingService.findByOwnerIdAndState(user1.getId(), "ALL", 0, 5);

        assertFalse(result.isEmpty());
        assertEquals(2, result.size());
    }

    @Test
    void findByOwnerIdAndState_caseFuture() {
        List<Booking> result = bookingService.findByOwnerIdAndState(user1.getId(), "FUTURE", 0, 5);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void findByOwnerIdAndState_casePast() {
        List<Booking> result = bookingService.findByOwnerIdAndState(user2.getId(), "PAST", 0, 5);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void findByOwnerIdAndState_caseCurrent() {
        List<Booking> result = bookingService.findByOwnerIdAndState(user1.getId(), "CURRENT", 0, 5);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void findByOwnerIdAndState_caseWaiting() {
        List<Booking> result = bookingService.findByOwnerIdAndState(user2.getId(), "WAITING", 0, 5);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void findLastBookingByItemId() {
        BookingInfoDto result = bookingService.findLastBookingByItemId(item2.getId());

        assertNotNull(result);
        assertEquals(2L, result.getId());
    }

    @Test
    void findNextBookingByItemId() {
        BookingInfoDto result = bookingService.findNextBookingByItemId(item1.getId());

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void hasUserBookedItem() {
        Boolean result = bookingService.hasUserBookedItem(user1.getId(), item1.getId());

        assertFalse(result);
    }

}