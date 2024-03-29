package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoComments;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {
    private final ItemService itemService;
    private static final String HEADER_USER_ID = "X-Sharer-User-Id";

    @GetMapping
    public Collection<ItemDtoComments> findAll(@RequestHeader(HEADER_USER_ID) int userId,
                                               @RequestParam Integer from,
                                               @RequestParam Integer size) {
        return itemService.findAll(userId, from, size);
    }

    @GetMapping("/{itemId}")
    public ItemDtoComments findById(@RequestHeader(HEADER_USER_ID) int userId,
                                    @PathVariable Integer itemId) {
        return itemService.findById(userId, itemId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> search(@RequestHeader(HEADER_USER_ID) int userId,
                                      @RequestParam String text,
                                      @RequestParam Integer from,
                                      @RequestParam Integer size) {
        return itemService.search(userId, text, from, size);
    }

    @PostMapping
    public ItemDto create(@RequestHeader(HEADER_USER_ID) int userId,
                          @RequestBody ItemDto itemDto) {
        return itemService.create(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader(HEADER_USER_ID) int userId,
                          @RequestBody ItemDto itemDto,
                          @PathVariable Integer itemId) {
        return itemService.update(userId, itemDto, itemId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestHeader(HEADER_USER_ID) int userId,
                                    @RequestBody Comment comment,
                                    @PathVariable Integer itemId) {
        return itemService.createComment(userId, comment, itemId);
    }
}
