package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoForItem;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.Collection;
import java.util.stream.Collectors;

public class BookingMapper {

    public static BookingDto toBookingDto(Booking booking) {
        return new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                UserMapper.toUserDto(booking.getBooker()),
                ItemMapper.toItemDto(booking.getItem()),
                booking.getStatus());
    }

    public static Collection<BookingDto> allToItemDto(Collection<Booking> bookings) {
        return bookings.stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
    }

    public static Booking toBooking(BookingDto bookingDto) {
        Booking booking = new Booking();
        booking.setId(bookingDto.getId());
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        booking.setBooker(UserMapper.toUser(bookingDto.getBooker()));
        booking.setItem(ItemMapper.toItem(bookingDto.getItem(), bookingDto.getBooker().getId()));
        booking.setStatus(bookingDto.getStatus());
        return booking;
    }

    public static BookingDto fromBookingDtoInputToBookingDto(BookingDtoInput bookingDtoInput) {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setStart(bookingDtoInput.getStart());
        bookingDto.setEnd(bookingDtoInput.getEnd());
        return bookingDto;
    }

    public static BookingDtoForItem bookingDtoForItem(Booking booking) {
        BookingDtoForItem bookingDtoForItem = new BookingDtoForItem();
        bookingDtoForItem.setId(booking.getId());
        bookingDtoForItem.setBookerId(booking.getBooker().getId());
        return bookingDtoForItem;
    }
}
