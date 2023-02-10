package ru.practicum.shareit.item.service;

import ru.practicum.shareit.exceptions.EntityBadRequest;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {
    Collection<ItemDto> findAll(Integer userId) throws EntityNotFoundException;

    ItemDto findById(Integer userId, Integer itemId) throws EntityNotFoundException;

    ItemDto create(Integer userId, ItemDto itemDto) throws EntityNotFoundException, EntityBadRequest;

    ItemDto update(Integer userId, ItemDto itemDto, Integer itemId) throws EntityNotFoundException;

    Collection<ItemDto> search(Integer userId, String text) throws EntityNotFoundException;
}
