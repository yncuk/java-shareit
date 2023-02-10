package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.user.User;

import java.util.Collection;

public interface UserStorage {
    Collection<User> findAll();

    User findById(Integer id) throws EntityNotFoundException;

    User create(User user);

    User update(Integer userId, User user);

    void delete(Integer userId);
}
