package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoToCreate;

import java.util.Collection;

public interface ItemRequestService {
    ItemRequestDtoToCreate create(Integer requesterId, ItemRequestDto itemRequestDto);

    Collection<ItemRequestDto> findAllByRequester(Integer requesterId);

    Collection<ItemRequestDto> findAllRequestsExceptRequester(int requesterId, Integer from, Integer size);

    ItemRequestDto findById(Integer requesterId, Integer requestId);
}
