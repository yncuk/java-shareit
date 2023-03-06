package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserStorage {
    Collection<User> findAll();

    User findById(Integer id);

    User create(User user);

    User update(Integer userId, User user);

    void softDelete(Integer userId);
}
