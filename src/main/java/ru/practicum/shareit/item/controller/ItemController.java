package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoComments;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {
    private final ItemService itemService;
    private final String headerUserId = "X-Sharer-User-Id";

    @GetMapping
    public Collection<ItemDtoComments> findAll(@RequestHeader(headerUserId) int userId,
                                               @RequestParam(required = false, defaultValue = "0") Integer from,
                                               @RequestParam(required = false, defaultValue = "10") Integer size) {
        return itemService.findAll(userId, from, size);
    }

    @GetMapping("/{itemId}")
    public ItemDtoComments findById(@RequestHeader(headerUserId) int userId, @PathVariable Integer itemId) {
        return itemService.findById(userId, itemId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> search(@RequestParam String text,
                                      @RequestParam(required = false, defaultValue = "0") Integer from,
                                      @RequestParam(required = false, defaultValue = "10") Integer size) {
        return itemService.search(text, from, size);
    }

    @PostMapping
    public ItemDto create(@RequestHeader(headerUserId) int userId, @Valid @RequestBody ItemDto itemDto) {
        return itemService.create(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader(headerUserId) int userId, @RequestBody ItemDto itemDto, @PathVariable Integer itemId) {
        return itemService.update(userId, itemDto, itemId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestHeader(headerUserId) int userId, @RequestBody Comment comment, @PathVariable Integer itemId) {
        return itemService.createComment(userId, comment, itemId);
    }
}
