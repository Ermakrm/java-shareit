package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@FieldDefaults(level = AccessLevel.PRIVATE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class BookingRepositoryTest {

    final PageRequest pageRequest = PageRequest.of(0, 10);
    private final LocalDateTime now = LocalDateTime.now();
    @Autowired
    BookingRepository bookingRepository;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    UserRepository userRepository;
    List<Booking> expectedBookings;
    List<Booking> result;
    User user1;
    User user2;

    Booking bookingFuture;
    Booking bookingCurrent;
    Booking bookingPast;

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
        Item item1 = itemRepository.save(i);

        Item i2 = new Item();
        i2.setName("secondItemName");
        i2.setDescription("beta");
        i2.setAvailable(true);
        i2.setOwner(user1);
        Item item2 = itemRepository.save(i2);

        Item i3 = new Item();
        i3.setName("thirdItemName");
        i3.setDescription("omega");
        i3.setAvailable(true);
        i3.setOwner(user2);
        Item item3 = itemRepository.save(i3);


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

    }

    @Test
    void findByBookerIdOrderByStartDesc() {
        expectedBookings = List.of(bookingFuture, bookingCurrent);

        result = bookingRepository.findByBookerIdOrderByStartDesc(user2.getId(), pageRequest);

        assertEquals(expectedBookings, result);
    }

    @Test
    void findByBookerIdAndStatusOrderByStartDesc() {
        expectedBookings = List.of(bookingFuture, bookingCurrent);

        result = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(
                user2.getId(), Status.APPROVED, pageRequest
        );

        assertEquals(expectedBookings, result);
    }

    @Test
    void findByBookerIdAndEndIsBeforeOrderByStartDesc_casePast() {

        expectedBookings = List.of(bookingPast);

        result = bookingRepository.findByBookerIdAndEndIsBeforeOrderByStartDesc(
                user1.getId(), now, pageRequest
        );

        assertEquals(expectedBookings, result);
    }

    @Test
    void findByBookerIdAndStartIsAfterOrderByStartDesc_caseFuture() {
        expectedBookings = List.of(bookingFuture);

        result = bookingRepository.findByBookerIdAndStartIsAfterOrderByStartDesc(
                user2.getId(), now, pageRequest
        );

        assertEquals(expectedBookings, result);
    }

    @Test
    void findAllByBookerIdAndStartBeforeAndEndAfterOrderByIdAsc_caseCurrent() {
        expectedBookings = List.of(bookingCurrent);

        result = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByIdAsc(
                user2.getId(), now, now, pageRequest
        );

        assertEquals(expectedBookings, result);
    }


    @Test
    void findByOwnerId() {
        expectedBookings = List.of(bookingFuture, bookingCurrent);

        result = bookingRepository.findByOwnerId(user1, pageRequest);

        assertEquals(expectedBookings, result);
    }

    @Test
    void findByOwnerIdAndStatus() {
        expectedBookings = List.of(bookingFuture, bookingCurrent);

        result = bookingRepository.findByOwnerIdAndStatus(user1, Status.APPROVED, pageRequest);

        assertEquals(expectedBookings, result);
    }

    @Test
    void findByOwnerIdInFuture() {
        expectedBookings = List.of(bookingFuture);

        result = bookingRepository.findByOwnerIdInFuture(user1, pageRequest);

        assertEquals(expectedBookings, result);
    }

    @Test
    void findByOwnerIdInPast() {
        expectedBookings = List.of(bookingPast);

        result = bookingRepository.findByOwnerIdInPast(user2, pageRequest);

        assertEquals(expectedBookings, result);
    }

    @Test
    void findByOwnerIdInCurrent() {
        expectedBookings = List.of(bookingCurrent);

        result = bookingRepository.findByOwnerIdInCurrent(user1, pageRequest);

        assertEquals(expectedBookings, result);
    }

}