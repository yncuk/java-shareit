package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exceptions.EntityBadRequestException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoComments;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public Collection<ItemDtoComments> findAll(@RequestHeader("X-Sharer-User-Id") int userId) {
        return itemService.findAll(userId);
    }

    @GetMapping("/{itemId}")
    public ItemDtoComments findById(@RequestHeader("X-Sharer-User-Id") int userId, @PathVariable Integer itemId) {
        return itemService.findById(userId, itemId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> search(@RequestParam String text) {
        return itemService.search(text);
    }

    @PostMapping
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") int userId, @RequestBody ItemDto itemDto) {
        if (itemDto.getAvailable() == null || itemDto.getName().isBlank() || itemDto.getName() == null ||
                itemDto.getDescription() == null || itemDto.getDescription().isBlank()) {
            throw new EntityBadRequestException("Не корректные данные в запросе");
        }
        return itemService.create(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") int userId, @RequestBody ItemDto itemDto, @PathVariable Integer itemId) {
        return itemService.update(userId, itemDto, itemId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestHeader("X-Sharer-User-Id") int userId, @RequestBody Comment comment, @PathVariable Integer itemId) {
        return itemService.createComment(userId, comment, itemId);
    }
}
