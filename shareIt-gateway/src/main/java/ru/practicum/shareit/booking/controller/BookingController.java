package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.BookingClient;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;
    private static final String HEADER_USER_ID = "X-Sharer-User-Id";

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> findById(@RequestHeader(HEADER_USER_ID) long userId,
                                           @PathVariable Long bookingId) {
        log.info("Get booking {}, userId={}", bookingId, userId);
        return bookingClient.findById(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> findAllByBooker(@RequestHeader(HEADER_USER_ID) long bookerId,
                                                  @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                                  @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                  @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, bookerId, from, size);
        return bookingClient.findAllByBooker(bookerId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> findAllByOwner(@RequestHeader(HEADER_USER_ID) long ownerId,
                                                 @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                                 @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                 @Positive @RequestParam(defaultValue = "10") Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        return bookingClient.findAllByOwner(ownerId, state, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(HEADER_USER_ID) long userId,
                                         @RequestBody @Valid BookItemRequestDto requestDto) {
        log.info("Creating booking {}, userId={}", requestDto, userId);
        return bookingClient.create(userId, requestDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> update(@RequestHeader(HEADER_USER_ID) long userId,
                                         @PathVariable long bookingId,
                                         @RequestParam Boolean approved) {
        return bookingClient.update(userId, bookingId, approved);
    }
}