package ru.practicum.shareit.item.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;

import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.EntityBadRequestException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoComments;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;

    @Override
    public Collection<ItemDtoComments> findAll(Integer userId) {
        userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("Не найден пользователь"));
        Collection<ItemDtoComments> items = ItemMapper.allToItemDtoWithComments(itemRepository.findItemsByOwnerOrderById(userId));
        for (ItemDtoComments currentItem : items) {
            Booking nextBooking = bookingRepository.findTop1ByItem_IdAndStartAfterAndStatusOrderByStartAsc(currentItem.getId(), LocalDateTime.now(), BookingStatus.APPROVED);
            if (nextBooking != null) {
                currentItem.setNextBooking(BookingMapper.bookingDtoForItem(nextBooking));
            }
            Booking lastBooking = bookingRepository.findTop1ByItem_IdAndEndBeforeAndStatusOrderByEndDesc(currentItem.getId(), LocalDateTime.now(), BookingStatus.APPROVED);
            if (lastBooking != null) {
                currentItem.setLastBooking(BookingMapper.bookingDtoForItem(lastBooking));
            }
        }
        return items;
    }

    @Override
    public ItemDtoComments findById(Integer userId, Integer itemId) {
        userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("Не найден пользователь"));
        ItemDtoComments itemDtoComments = ItemMapper.toItemDtoWithComments(itemRepository.getReferenceById(itemId));
        Booking nextBooking = bookingRepository.findTop1ByItem_IdAndStartAfterAndStatusAndItem_OwnerOrderByStartAsc(itemId, LocalDateTime.now(), BookingStatus.APPROVED, userId);
        if (nextBooking != null) {
            itemDtoComments.setNextBooking(BookingMapper.bookingDtoForItem(nextBooking));
        }
        Booking lastBooking = bookingRepository.findLastBookingByItem(itemId, LocalDateTime.now(), BookingStatus.APPROVED, userId);
        if (lastBooking != null) {
            itemDtoComments.setLastBooking(BookingMapper.bookingDtoForItem(lastBooking));
        }
        return itemDtoComments;
    }

    @Override
    public ItemDto create(Integer userId, ItemDto itemDto) {
        userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("Не найден пользователь"));
        return ItemMapper.toItemDto(itemRepository.save(ItemMapper.toItem(itemDto, userId)));
    }

    @Override
    public ItemDto update(Integer userId, ItemDto itemDto, Integer itemId) {
        userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("Не найден пользователь"));
        if (itemRepository.findItemsByOwnerOrderById(userId).isEmpty()) {
            throw new EntityNotFoundException("Не найдены вещи у пользователя");
        }
        Item newItem = itemRepository.getReferenceById(itemId);
        if (itemDto.getAvailable() != null) {
            newItem.setAvailable(itemDto.getAvailable());
            log.info("Задали новый статус вещи {}", itemDto.getAvailable());
        }
        if (itemDto.getDescription() != null) {
            newItem.setDescription(itemDto.getDescription());
            log.info("Задали новое описание вещи {}", itemDto.getDescription());
        }
        if (itemDto.getName() != null) {
            newItem.setName(itemDto.getName());
            log.info("Задали новое название вещи {}", itemDto.getName());
        }
        return ItemMapper.toItemDto(itemRepository.save(newItem));
    }

    @Override
    public Collection<ItemDto> search(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        return ItemMapper.allToItemDto(itemRepository.search(text));
    }

    @Override
    public CommentDto createComment(Integer userId, Comment comment, Integer itemId) {
        comment.setAuthor(userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("Не найден пользователь")));
        itemRepository.findById(itemId).orElseThrow(() -> new EntityNotFoundException("Не найдена вещь"));
        bookingRepository.findByBookerIdAndItemId(userId, itemId, BookingStatus.APPROVED, LocalDateTime.now()).orElseThrow(() -> new EntityBadRequestException("У пользователя нет бронирования этой вещи"));
        comment.setCreated(LocalDateTime.now());
        comment.setItemId(itemId);
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }
}
