package ru.practicum.shareit.request.mapper;

import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.request.dto.ItemRequestDtoToCreate;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

public class ItemRequestMapper {
    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return new ItemRequestDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getCreated(),
                new ArrayList<>(ItemMapper.allToItemDto(itemRequest.getItems())));
    }

    public static ItemRequestDtoToCreate toItemRequestDtoToCreate(ItemRequest itemRequest) {
        return new ItemRequestDtoToCreate(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getCreated());
    }

    public static Collection<ItemRequestDto> allToItemRequestDto(Collection<ItemRequest> itemRequest) {
        return itemRequest.stream().map(ItemRequestMapper::toItemRequestDto).collect(Collectors.toList());
    }

    public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(itemRequestDto.getId());
        itemRequest.setDescription(itemRequestDto.getDescription());
        return itemRequest;
    }
}
