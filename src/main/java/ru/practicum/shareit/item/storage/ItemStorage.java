package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.List;

public interface ItemStorage {
    Collection<Item> findAll(Integer userId);

    Item findById(Integer userId, Integer itemId) throws EntityNotFoundException;

    Item create(Integer userId, Item item);

    Item update(Integer userId, ItemDto itemDto, Integer itemId) throws EntityNotFoundException;

    List<Item> search(Integer userId, String text);

}
