package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoComments;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.stream.Collectors;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                null,
                null,
                item.getRequestId());
    }

    public static Collection<ItemDto> allToItemDto(Collection<Item> items) {
        return items.stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    public static Item toItem(ItemDto itemDto, Integer userId) {
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setDescription(itemDto.getDescription());
        item.setName(itemDto.getName());
        item.setAvailable(itemDto.getAvailable());
        item.setOwner(userId);
        if (itemDto.getRequestId() != null) {
            item.setRequestId(itemDto.getRequestId());
        }
        return item;
    }

    public static ItemDtoComments toItemDtoWithComments(Item item) {
        return new ItemDtoComments(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                null,
                null,
                CommentMapper.allToCommentDto(item.getComments()));
    }

    public static Collection<ItemDtoComments> allToItemDtoWithComments(Collection<Item> items) {
        return items.stream().map(ItemMapper::toItemDtoWithComments).collect(Collectors.toList());
    }
}
