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

    private final String headerUserId = "X-Sharer-User-Id";
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDtoToCreate create(@RequestHeader(headerUserId) int requesterId, @Valid @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestService.create(requesterId, itemRequestDto);
    }

    @GetMapping
    public Collection<ItemRequestDto> findAllByRequester(@RequestHeader(headerUserId) int requesterId) {
        return itemRequestService.findAllByRequester(requesterId);
    }

    @GetMapping("/all")
    public Collection<ItemRequestDto> findAll(@RequestHeader(headerUserId) int requesterId,
                                              @RequestParam(required = false, defaultValue = "0") Integer from,
                                              @RequestParam(required = false, defaultValue = "10") Integer size) {
        return itemRequestService.findAll(requesterId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto findById(@RequestHeader(headerUserId) int requesterId, @PathVariable Integer requestId) {
        return itemRequestService.findById(requesterId, requestId);
    }
}
