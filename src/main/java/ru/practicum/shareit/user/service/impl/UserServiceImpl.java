package ru.practicum.shareit.user.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ConflictException;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;

    @Override
    public Collection<UserDto> findAll() {
        return UserMapper.allToUserDto(userStorage.findAll());
    }

    @Override
    public UserDto findById(Integer id) throws EntityNotFoundException {
        return UserMapper.toUserDto(userStorage.findById(id));
    }

    @Override
    public UserDto create(User user) throws ConflictException {
        if (isEmailFound(user.getEmail())) {
            throw new ConflictException("Пользователь с такой почтой уже есть");
        }
        return UserMapper.toUserDto(userStorage.create(user));
    }

    @Override
    public UserDto update(Integer userId, User user) throws EntityNotFoundException, ConflictException {
        userStorage.findById(userId);
        User newUser = userStorage.findById(userId);
        if (user.getEmail() != null) {
            if (!isEmailFound(user.getEmail())) {
                newUser = newUser.withEmail(user.getEmail());
                log.info("Задаем новую почту пользователю - {}", user.getEmail());
            } else throw new ConflictException("Пользователь с такой почтой уже есть");
        }
        if (user.getName() != null) {
            newUser = newUser.withName(user.getName());
            log.info("Задаем новое имя пользователю - {}", user.getName());
        }
        return UserMapper.toUserDto(userStorage.update(userId, newUser));
    }

    @Override
    public void delete(Integer userId) {
        userStorage.delete(userId);
    }

    private Boolean isEmailFound(String email) {
        for (User currentUser : userStorage.findAll()) {
            if (currentUser.getEmail().equals(email)) {
                return true;
            }
        }
        return false;
    }
}
