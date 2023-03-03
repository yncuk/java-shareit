package ru.practicum.shareit.booking.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Entity
@Table(name = "BOOKING")
@Getter
@Setter
@ToString
public class Booking {
    @Id
    @Column(name = "BOOKING_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "START_TIME")
    LocalDateTime start;

    @Column(name = "END_TIME")
    LocalDateTime end;

    @ManyToOne
    @JoinColumn(name = "BOOKER_ID", referencedColumnName = "USER_ID")
    User booker;

    @OneToOne
    @JoinColumn(name = "ITEM_ID", referencedColumnName = "ITEM_ID")
    Item item;

    @Enumerated(EnumType.STRING)
    BookingStatus status;
}
