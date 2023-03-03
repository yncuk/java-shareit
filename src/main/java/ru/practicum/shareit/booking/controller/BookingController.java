package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.Collection;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @GetMapping("/{bookingId}")
    public BookingDto findById(@RequestHeader("X-Sharer-User-Id") int userId, @PathVariable Integer bookingId) {
        return bookingService.findById(userId, bookingId);
    }

    @GetMapping
    public Collection<BookingDto> findAllByBooker(@RequestHeader("X-Sharer-User-Id") int bookerId, @RequestParam(required = false, defaultValue = "ALL") String state) {
        return bookingService.findAllByBooker(bookerId, state);
    }

    @GetMapping("/owner")
    public Collection<BookingDto> findAllByOwner(@RequestHeader("X-Sharer-User-Id") int ownerId, @RequestParam(required = false, defaultValue = "ALL") String state) {
        return bookingService.findAllByOwner(ownerId, state);
    }

    @PostMapping
    public BookingDto create(@RequestHeader("X-Sharer-User-Id") int userId, @RequestBody BookingDtoInput bookingDtoInput) {
        return bookingService.create(userId, bookingDtoInput);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto update(@RequestHeader("X-Sharer-User-Id") int userId, @PathVariable Integer bookingId, @RequestParam Boolean approved) {
        return bookingService.update(userId, bookingId, approved);
    }
}
