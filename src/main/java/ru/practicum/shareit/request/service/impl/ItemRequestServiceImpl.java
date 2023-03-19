package ru.practicum.shareit.request.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.EntityBadRequestException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoToCreate;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;

    @Override
    public ItemRequestDtoToCreate create(Integer requesterId, ItemRequestDto itemRequestDto) {
        ItemRequest newItemRequest = ItemRequestMapper.toItemRequest(itemRequestDto);
        newItemRequest.setRequester(userRepository.findById(requesterId)
                .orElseThrow(() -> new EntityNotFoundException("Не найден пользователь")));
        newItemRequest.setCreated(LocalDateTime.now());
        return ItemRequestMapper.toItemRequestDtoToCreate(itemRequestRepository.save(newItemRequest));
    }

    @Override
    public Collection<ItemRequestDto> findAllByRequester(Integer requesterId) {
        userRepository.findById(requesterId).orElseThrow(() -> new EntityNotFoundException("Не найден пользователь"));
        return ItemRequestMapper.allToItemRequestDto(itemRequestRepository.findAllByRequester_Id(requesterId));
    }

    @Override
    public Collection<ItemRequestDto> findAll(int requesterId, Integer from, Integer size) {
        if (from < 0 || size <= 0) {
            throw new EntityBadRequestException("Количество пропущенных элементов и страниц не должно быть меньше 0");
        }
        userRepository.findById(requesterId).orElseThrow(() -> new EntityNotFoundException("Не найден пользователь"));
        Collection<ItemRequest> itemRequests = itemRequestRepository.findAllWithoutPropertyThisUser(requesterId);
        if (itemRequests.isEmpty()) {
            return new ArrayList<>();
        }
        return ItemRequestMapper.allToItemRequestDto(itemRequests.stream().skip(from).limit(size).collect(Collectors.toList()));
    }

    @Override
    public ItemRequestDto findById(Integer requesterId, Integer requestId) {
        userRepository.findById(requesterId).orElseThrow(() -> new EntityNotFoundException("Не найден пользователь"));
        return ItemRequestMapper.toItemRequestDto(itemRequestRepository.findById(requestId).orElseThrow(EntityNotFoundException::new));
    }
}
