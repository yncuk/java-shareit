package ru.practicum.shareit.item.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class InMemoryItemStorage implements ItemStorage {

    private final Map<Integer, Map<Integer, Item>> items = new HashMap<>();
    private int id = 1;


    @Override
    public Collection<Item> findAll(Integer userId) {
        Map<Integer, Item> userItems = items.get(userId);
        if (userItems == null) {
            return new ArrayList<>();
        }
        return userItems.values();
    }

    @Override
    public Item findById(Integer itemId) {
        return giveAllItem().get(itemId);
    }

    @Override
    public Item create(Integer userId, Item item) {
        if (!items.containsKey(userId)) {
            items.put(userId, new HashMap<>());
            log.info("Создали место для вещей пользователю с id - {}", userId);
        }
        item = item.withId(id);
        item = item.withOwner(userId);
        items.get(userId).put(id, item);
        id++;
        log.info("Создали новую вещь {}", item);
        return item;
    }

    @Override
    public Item update(Integer userId, ItemDto itemDto, Integer itemId) {
        if (items.get(userId).containsKey(itemId)) {
            Item newItem = items.get(userId).get(itemId);
            if (itemDto.getAvailable() != null) {
                newItem = newItem.withAvailable(itemDto.getAvailable());
                log.info("Задали новый статус вещи {}", itemDto.getAvailable());
            }
            if (itemDto.getDescription() != null) {
                newItem = newItem.toBuilder().description(itemDto.getDescription()).build();
                log.info("Задали новое описание вещи {}", itemDto.getDescription());
            }
            if (itemDto.getName() != null) {
                newItem = newItem.toBuilder().name(itemDto.getName()).build();
                log.info("Задали новое название вещи {}", itemDto.getName());
            }
            items.get(userId).put(itemId, newItem);
            return newItem;
        } else throw new EntityNotFoundException("Не найдена вещь для обновления");
    }

    @Override
    public List<Item> search(String text) {
        List<Item> foundItem = new ArrayList<>();
        if (text.isBlank()) {
            return foundItem;
        }
        for (Item currentItem : giveAllItem().values()) {
            if (currentItem.getAvailable() && (currentItem.getName().toLowerCase().contains(text.toLowerCase()) ||
                    currentItem.getDescription().toLowerCase().contains(text.toLowerCase()))) {
                foundItem.add(currentItem);
            }
        }
        return foundItem;
    }

    private Map<Integer, Item> giveAllItem() {
        Map<Integer, Item> allItems = new HashMap<>();
        for (Map<Integer, Item> currentItems : items.values()) {
            allItems.putAll(currentItems);
        }
        return allItems;
    }
}
