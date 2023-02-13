package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.List;

public interface ItemStorage {
    Collection<Item> findAll(Integer userId);

    Item findById(Integer itemId);

    Item create(Integer userId, Item item);

    Item update(Integer userId, ItemDto itemDto, Integer itemId);

    List<Item> search(String text);

}
