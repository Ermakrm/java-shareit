package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerIdOrderByStartDesc(Long bookerId);

    List<Booking> findByBookerIdAndStatusOrderByStartDesc(Long bookerId, Status status);

    List<Booking> findByBookerIdAndEndIsBeforeOrderByStartDesc(Long bookerId, LocalDateTime end);

    List<Booking> findByBookerIdAndStartIsAfterOrderByStartDesc(Long bookerId, LocalDateTime start);

    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByIdAsc(Long bookerId, LocalDateTime start,
                                                                         LocalDateTime end);

    List<Booking> findByBookerIdAndItemIdAndEndIsBeforeAndStatusOrderByStartDesc(Long bookerId, Long itemId, LocalDateTime now,
                                                                                 Status status);

    @Query("SELECT b FROM Booking b, Item i WHERE b.item.id = i.id AND i.owner = :owner " +
            "ORDER BY b.start DESC")
    List<Booking> findByOwnerId(@Param("owner") User owner);

    @Query("SELECT b FROM Booking b, Item i WHERE b.item.id = i.id AND i.owner = :owner AND " +
            "b.status = :status ORDER BY b.start DESC")
    List<Booking> findByOwnerIdAndStatus(@Param("owner") User owner, @Param("status") Status status);

    @Query("SELECT b FROM Booking b, Item i WHERE b.item.id = i.id AND i.owner = :owner AND " +
            "b.start > CURRENT_TIMESTAMP ORDER BY b.start DESC")
    List<Booking> findByOwnerIdInFuture(@Param("owner") User owner);

    @Query("SELECT b FROM Booking b, Item i WHERE b.item.id = i.id AND i.owner = :owner AND " +
            "b.end < CURRENT_TIMESTAMP ORDER BY b.start DESC")
    List<Booking> findByOwnerIdInPast(@Param("owner") User owner);

    @Query("SELECT b FROM Booking b, Item i WHERE b.item.id = i.id AND i.owner = :owner AND " +
            "b.start <= CURRENT_TIMESTAMP AND b.end >= CURRENT_TIMESTAMP ORDER BY b.start DESC")
    List<Booking> findByOwnerIdInCurrent(@Param("owner") User owner);

    @Query(value = "SELECT * FROM Bookings WHERE item_id = :itemId AND start_date < CURRENT_TIMESTAMP AND " +
            "status = 'APPROVED' ORDER BY start_date DESC LIMIT 1", nativeQuery = true)
    Booking findLastBookingByItemId(@Param("itemId") Long itemId);

    @Query(value = "SELECT * FROM Bookings WHERE item_id = :itemId AND start_date > CURRENT_TIMESTAMP AND " +
            "status = 'APPROVED' ORDER BY start_date LIMIT 1", nativeQuery = true)
    Booking findNextBookingByItemId(@Param("itemId") Long itemId);

}
