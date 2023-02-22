package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

public interface UserService {

    Collection<UserDto> findAll();

    UserDto findById(Integer id);

    UserDto create(UserDto userDto);

    UserDto update(Integer userId, UserDto userDto);

    void delete(Integer userId);
}
