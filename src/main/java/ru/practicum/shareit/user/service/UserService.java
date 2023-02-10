package ru.practicum.shareit.user.service;

import ru.practicum.shareit.exceptions.ConflictException;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

public interface UserService {

    Collection<UserDto> findAll();

    UserDto findById(Integer id) throws EntityNotFoundException;

    UserDto create(User user) throws ConflictException;

    UserDto update(Integer userId, User user) throws EntityNotFoundException, ConflictException;

    void delete(Integer userId);
}
