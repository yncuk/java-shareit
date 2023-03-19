package ru.practicum.shareit.booking.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exceptions.EntityBadRequestException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public BookingDto findById(Integer userId, Integer bookingId) {
        userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("Не найден пользователь"));
        return BookingMapper.toBookingDto(bookingRepository.findByBookerIdOrOwnerIdAndBookingId(userId, userId, bookingId).orElseThrow(() -> new EntityNotFoundException("У пользователя нет этого бронирования")));
    }

    @Override
    public Collection<BookingDto> findAllByBooker(Integer bookerId, String state, Integer from, Integer size) {
        if (from < 0 || size <= 0) {
            throw new EntityBadRequestException("Количество пропущенных элементов и страниц не должно быть меньше 0");
        }
        userRepository.findById(bookerId).orElseThrow(() -> new EntityNotFoundException("Не найден пользователь"));
        switch (state) {
            case "ALL":
                return BookingMapper.allToBookingDto(bookingRepository.findAllByBooker(bookerId).stream().skip(from).limit(size).collect(Collectors.toList()));
            case "CURRENT":
                return BookingMapper.allToBookingDto(bookingRepository.findByBooker_IdAndStartBeforeAndEndAfterOrderByStartDesc(bookerId, LocalDateTime.now(), LocalDateTime.now()).stream().skip(from).limit(size).collect(Collectors.toList()));
            case "PAST":
                return BookingMapper.allToBookingDto(bookingRepository.findByBooker_IdAndEndBeforeOrderByStartDesc(bookerId, LocalDateTime.now()).stream().skip(from).limit(size).collect(Collectors.toList()));
            case "FUTURE":
                return BookingMapper.allToBookingDto(bookingRepository.findByBooker_IdAndStartAfterOrderByStartDesc(bookerId, LocalDateTime.now()).stream().skip(from).limit(size).collect(Collectors.toList()));
            case "WAITING":
            case "REJECTED":
                return BookingMapper.allToBookingDto(bookingRepository.findByBooker_IdAndStatusIsOrderByStartDesc(bookerId, BookingStatus.valueOf(state)).stream().skip(from).limit(size).collect(Collectors.toList()));
            default:
                throw new EntityBadRequestException(String.format("Unknown state: %S", state));
        }
    }

    @Override
    public Collection<BookingDto> findAllByOwner(Integer ownerId, String state, Integer from, Integer size) {
        if (from < 0 || size <= 0) {
            throw new EntityBadRequestException("Количество пропущенных элементов и страниц не должно быть меньше 0");
        }
        userRepository.findById(ownerId).orElseThrow(() -> new EntityNotFoundException("Не найден пользователь"));
        switch (state) {
            case "ALL":
                return BookingMapper.allToBookingDto(bookingRepository.findAllByOwner(ownerId).stream().skip(from).limit(size).collect(Collectors.toList()));
            case "CURRENT":
                return BookingMapper.allToBookingDto(bookingRepository.findByItem_OwnerAndStartBeforeAndEndAfterOrderByStartDesc(ownerId, LocalDateTime.now(), LocalDateTime.now()).stream().skip(from).limit(size).collect(Collectors.toList()));
            case "PAST":
                return BookingMapper.allToBookingDto(bookingRepository.findByItem_OwnerAndEndBeforeOrderByStartDesc(ownerId, LocalDateTime.now()).stream().skip(from).limit(size).collect(Collectors.toList()));
            case "FUTURE":
                return BookingMapper.allToBookingDto(bookingRepository.findByItem_OwnerAndStartAfterOrderByStartDesc(ownerId, LocalDateTime.now()).stream().skip(from).limit(size).collect(Collectors.toList()));
            case "WAITING":
            case "REJECTED":
                return BookingMapper.allToBookingDto(bookingRepository.findByItem_OwnerAndStatusIsOrderByStartDesc(ownerId, BookingStatus.valueOf(state)).stream().skip(from).limit(size).collect(Collectors.toList()));
            default:
                throw new EntityBadRequestException(String.format("Unknown state: %S", state));
        }
    }

    @Override
    public BookingDto create(Integer userId, BookingDtoInput bookingDtoInput) {
        userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("Не найден пользователь"));
        BookingDto bookingDto = BookingMapper.fromBookingDtoInputToBookingDto(bookingDtoInput);
        itemRepository.findById(bookingDtoInput.getItemId()).orElseThrow(() -> new EntityNotFoundException("Не найдена вещь"));
        if (!itemRepository.getReferenceById(bookingDtoInput.getItemId()).getAvailable()) {
            throw new EntityBadRequestException("Вещь недоступна для бронирования");
        }
        if (bookingDto.getStart().isAfter(bookingDto.getEnd()) || bookingDto.getStart().isBefore(LocalDateTime.now()) || bookingDto.getEnd().isBefore(LocalDateTime.now())) {
            throw new EntityBadRequestException("Неправильно задано время бронирования");
        }
        if (bookingDto.getStart().equals(bookingDto.getEnd())) {
            throw new EntityBadRequestException("Время начала и окончания бронирования не может совпадать");
        }
        bookingDto.setStatus(BookingStatus.WAITING);
        bookingDto.setBooker(UserMapper.toUserDto(userRepository.getReferenceById(userId)));
        Item bookingItem = itemRepository.getReferenceById(bookingDtoInput.getItemId());
        if (bookingItem.getOwner() == userId) {
            throw new EntityNotFoundException("Вещь не доступна для бронирования владельцу");
        }
        bookingDto.setItem(ItemMapper.toItemDto(bookingItem));
        return BookingMapper.toBookingDto(bookingRepository.save(BookingMapper.toBooking(bookingDto)));
    }

    @Override
    public BookingDto update(Integer userId, Integer bookingId, Boolean approved) {
        userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("Не найден пользователь"));
        Booking booking = bookingRepository.findByOwnerIdAndBookingId(userId, bookingId).orElseThrow(() -> new EntityNotFoundException("У пользователя нет этого бронирования с id = " + bookingId));
        if (approved) {
            if (booking.getStatus().equals(BookingStatus.WAITING)) {
                booking.setStatus(BookingStatus.APPROVED);
            } else throw new EntityBadRequestException("Неправильно задан статус бронирования");
        } else {
            if (booking.getStatus().equals(BookingStatus.WAITING)) {
                booking.setStatus(BookingStatus.REJECTED);
            } else throw new EntityBadRequestException("Неправильно задан статус бронирования");
        }
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }


}
