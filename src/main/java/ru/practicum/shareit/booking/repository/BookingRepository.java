package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

    @Query(" select b from Booking b " +
            "where (b.booker.id = ?1 or b.item.owner = ?2) and b.id = ?3 ")
    Optional<Booking> findByBookerIdOrOwnerIdAndBookingId(Integer bookerId, Integer ownerId, Integer bookingId);

    @Query(" select b from Booking b " +
            "where b.item.owner = ?1 and b.id = ?2 ")
    Optional<Booking> findByOwnerIdAndBookingId(Integer userId, Integer bookingId);

    @Query(" select b from Booking b " +
            "where b.booker.id = ?1 and b.item.id = ?2 and b.status = ?3 and b.end < ?4")
    Optional<Booking> findByBookerIdAndItemId(Integer userId, Integer itemId, BookingStatus status, LocalDateTime now);


    Booking findTop1ByItem_IdAndEndBeforeAndStatusOrderByEndDesc(int item_id, LocalDateTime end, BookingStatus status);

    Booking findTop1ByItem_IdAndStartAfterAndStatusOrderByStartAsc(int item_id, LocalDateTime start, BookingStatus status);

    @Query( " select b from Booking b " +
            "where b.item.id = ?1 and b.end < ?2 and b.status = ?3 and b.item.owner = ?4 " +
            "order by b.end desc ")
    Booking findLastBookingByItem(Integer itemId, LocalDateTime now, BookingStatus status, Integer userId);

    Booking findTop1ByItem_IdAndStartAfterAndStatusAndItem_OwnerOrderByStartAsc(Integer itemId, LocalDateTime now, BookingStatus status, Integer userId);

    @Query(" select b from Booking b " +
            "where b.booker.id = ?1 " +
            "order by b.start desc ")
    Collection<Booking> findAllByBooker(Integer bookerId);

    Collection<Booking> findByBooker_IdAndStatusIsOrderByStartDesc(Integer bookerId, BookingStatus state);

    Collection<Booking> findByBooker_IdAndEndBeforeOrderByStartDesc(Integer bookerId, LocalDateTime end);

    Collection<Booking> findByBooker_IdAndStartAfterOrderByStartDesc(Integer bookerId, LocalDateTime start);

    Collection<Booking> findByBooker_IdAndStartBeforeAndEndAfterOrderByStartDesc(Integer bookerId, LocalDateTime start, LocalDateTime end);

    @Query(" select b from Booking b " +
            "where b.item.owner = ?1 " +
            "order by b.start desc ")
    Collection<Booking> findAllByOwner(Integer ownerId);

    Collection<Booking> findByItem_OwnerAndStatusIsOrderByStartDesc(Integer ownerId, BookingStatus state);

    Collection<Booking> findByItem_OwnerAndEndBeforeOrderByStartDesc(Integer ownerId, LocalDateTime end);

    Collection<Booking> findByItem_OwnerAndStartAfterOrderByStartDesc(Integer ownerId, LocalDateTime start);

    Collection<Booking> findByItem_OwnerAndStartBeforeAndEndAfterOrderByStartDesc(Integer ownerId, LocalDateTime start, LocalDateTime end);

}
