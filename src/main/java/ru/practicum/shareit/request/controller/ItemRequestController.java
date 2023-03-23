package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoToCreate;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {

    private static final String HEADER_USER_ID = "X-Sharer-User-Id";
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDtoToCreate create(@RequestHeader(HEADER_USER_ID) int requesterId, @Valid @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestService.create(requesterId, itemRequestDto);
    }

    @GetMapping
    public Collection<ItemRequestDto> findAllByRequester(@RequestHeader(HEADER_USER_ID) int requesterId) {
        return itemRequestService.findAllByRequester(requesterId);
    }

    @GetMapping("/all")
    public Collection<ItemRequestDto> findAllRequestsExceptRequester(@RequestHeader(HEADER_USER_ID) int requesterId,
                                                                     @RequestParam(required = false, defaultValue = "0") Integer from,
                                                                     @RequestParam(required = false, defaultValue = "10") Integer size) {
        return itemRequestService.findAllRequestsExceptRequester(requesterId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto findById(@RequestHeader(HEADER_USER_ID) int requesterId, @PathVariable Integer requestId) {
        return itemRequestService.findById(requesterId, requestId);
    }
}
