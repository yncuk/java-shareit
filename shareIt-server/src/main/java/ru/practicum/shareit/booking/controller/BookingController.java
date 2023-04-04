package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.Collection;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;
    private static final String HEADER_USER_ID = "X-Sharer-User-Id";

    @GetMapping("/{bookingId}")
    public BookingDto findById(@RequestHeader(HEADER_USER_ID) int userId,
                               @PathVariable Integer bookingId) {
        return bookingService.findById(userId, bookingId);
    }

    @GetMapping
    public Collection<BookingDto> findAllByBooker(@RequestHeader(HEADER_USER_ID) int bookerId,
                                                  @RequestParam String state,
                                                  @RequestParam Integer from,
                                                  @RequestParam Integer size) {
        return bookingService.findAllByBooker(bookerId, state, from, size);
    }

    @GetMapping("/owner")
    public Collection<BookingDto> findAllByOwner(@RequestHeader(HEADER_USER_ID) int ownerId,
                                                 @RequestParam String state,
                                                 @RequestParam Integer from,
                                                 @RequestParam Integer size) {
        return bookingService.findAllByOwner(ownerId, state, from, size);
    }

    @PostMapping
    public BookingDto create(@RequestHeader(HEADER_USER_ID) int userId,
                             @RequestBody BookingDtoInput bookingDtoInput) {
        return bookingService.create(userId, bookingDtoInput);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto update(@RequestHeader(HEADER_USER_ID) int userId,
                             @PathVariable Integer bookingId,
                             @RequestParam Boolean approved) {
        return bookingService.update(userId, bookingId, approved);
    }
}
