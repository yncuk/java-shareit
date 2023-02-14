package ru.practicum.shareit.user.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ConflictException;
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
    public UserDto findById(Integer id) {
        return UserMapper.toUserDto(userStorage.findById(id));
    }

    @Override
    public UserDto create(UserDto userDto) {
        if (isEmailFound(userDto.getEmail())) {
            throw new ConflictException("Пользователь с такой почтой уже есть");
        }
        return UserMapper.toUserDto(userStorage.create(UserMapper.toUser(userDto)));
    }

    @Override
    public UserDto update(Integer userId, UserDto userDto) {
        userStorage.findById(userId);
        User newUser = userStorage.findById(userId);
        if (userDto.getEmail() != null) {
            if (!isEmailFound(userDto.getEmail()) || newUser.getEmail().equals(userDto.getEmail())) {
                newUser = newUser.toBuilder().email(userDto.getEmail()).build();
                log.info("Задаем новую почту пользователю - {}", userDto.getEmail());
            } else throw new ConflictException("Пользователь с такой почтой уже есть");
        }
        if (userDto.getName() != null) {
            newUser = newUser.toBuilder().name(userDto.getName()).build();
            log.info("Задаем новое имя пользователю - {}", userDto.getName());
        }
        return UserMapper.toUserDto(userStorage.update(userId, newUser));
    }

    @Override
    public void delete(Integer userId) {
        userStorage.softDelete(userId);
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
