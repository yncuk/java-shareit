package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoComments;
import ru.practicum.shareit.item.model.Comment;

import java.util.Collection;

public interface ItemService {
    Collection<ItemDtoComments> findAll(Integer userId, Integer from, Integer size);

    ItemDtoComments findById(Integer userId, Integer itemId);

    ItemDto create(Integer userId, ItemDto itemDto);

    ItemDto update(Integer userId, ItemDto itemDto, Integer itemId);

    Collection<ItemDto> search(String text, Integer from, Integer size);

    CommentDto createComment(Integer userId, Comment comment, Integer itemId);
}
