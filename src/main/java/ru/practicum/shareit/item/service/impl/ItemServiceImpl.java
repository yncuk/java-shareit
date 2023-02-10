package ru.practicum.shareit.item.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.EntityBadRequest;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Override
    public Collection<ItemDto> findAll(Integer userId) throws EntityNotFoundException {
        userStorage.findById(userId);
        return ItemMapper.allToItemDto(itemStorage.findAll(userId));
    }

    @Override
    public ItemDto findById(Integer userId, Integer itemId) throws EntityNotFoundException {
        userStorage.findById(userId);
        return ItemMapper.toItemDto(itemStorage.findById(userId, itemId));
    }

    @Override
    public ItemDto create(Integer userId, ItemDto itemDto) throws EntityNotFoundException, EntityBadRequest {
        if (itemDto.getAvailable() == null || itemDto.getName().isBlank() || itemDto.getName() == null ||
                itemDto.getDescription() == null || itemDto.getDescription().isBlank()) {
            throw new EntityBadRequest("Не корректные данные в запросе");
        }
        userStorage.findById(userId);
        return ItemMapper.toItemDto(itemStorage.create(userId, ItemMapper.toItem(itemDto)));
    }

    @Override
    public ItemDto update(Integer userId, ItemDto itemDto, Integer itemId) throws EntityNotFoundException {
        userStorage.findById(userId);
        if (itemStorage.findAll(userId).isEmpty()) {
            throw new EntityNotFoundException("Не найдены вещи у пользователя");
        }
        return ItemMapper.toItemDto(itemStorage.update(userId, itemDto, itemId));
    }

    @Override
    public Collection<ItemDto> search(Integer userId, String text) throws EntityNotFoundException {
        userStorage.findById(userId);
        return ItemMapper.allToItemDto(itemStorage.search(userId, text));
    }
}
