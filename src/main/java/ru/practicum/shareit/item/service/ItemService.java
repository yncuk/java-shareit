package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {
    Collection<ItemDto> findAll(Integer userId);

    ItemDto findById(Integer itemId);

    ItemDto create(Integer userId, ItemDto itemDto);

    ItemDto update(Integer userId, ItemDto itemDto, Integer itemId);

    Collection<ItemDto> search(String text);
}
