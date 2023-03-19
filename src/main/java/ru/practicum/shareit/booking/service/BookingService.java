package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoInput;

import java.util.Collection;

public interface BookingService {

    BookingDto findById(Integer userId, Integer bookingId);

    Collection<BookingDto> findAllByBooker(Integer bookerId, String state, Integer from, Integer size);

    Collection<BookingDto> findAllByOwner(Integer ownerId, String state, Integer from, Integer size);

    BookingDto create(Integer userId, BookingDtoInput bookingDtoInput);

    BookingDto update(Integer userId, Integer bookingId, Boolean approved);
}
