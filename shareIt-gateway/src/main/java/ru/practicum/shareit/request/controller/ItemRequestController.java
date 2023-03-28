package ru.practicum.shareit.request.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.ItemRequestClient;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;
    private static final String HEADER_USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(HEADER_USER_ID) long requesterId,
                                         @RequestBody @Valid ItemRequestDto itemRequestDto) {
        return itemRequestClient.create(requesterId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> findAllByRequester(@RequestHeader(HEADER_USER_ID) long requesterId) {
        return itemRequestClient.findAllByRequester(requesterId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> findAllRequestsExceptRequester(@RequestHeader(HEADER_USER_ID) long requesterId,
                                                                 @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                                 @Positive @RequestParam(defaultValue = "10") Integer size) {
        return itemRequestClient.findAllRequestsExceptRequester(requesterId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> findById(@RequestHeader(HEADER_USER_ID) long requesterId,
                                           @PathVariable long requestId) {
        return itemRequestClient.findById(requesterId, requestId);
    }
}
