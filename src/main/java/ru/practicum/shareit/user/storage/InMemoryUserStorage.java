package ru.practicum.shareit.user.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.user.User;

import java.util.*;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> users = new HashMap<>();
    private int id = 1;

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public User findById(Integer id) throws EntityNotFoundException {
        if (users.containsKey(id)) {
            return users.get(id);
        } else throw new EntityNotFoundException("Не найден пользователь");
    }

    @Override
    public User create(User user) {
        user = user.withId(id);
        users.put(id, user);
        id++;
        log.info("Создаем пользователя {}", user);
        return user;
    }

    @Override
    public User update(Integer userId, User user) {
        users.put(userId, user);
        log.info("Обновляем пользователя {}", user);
        return user;
    }

    @Override
    public void delete(Integer userId) {
        users.remove(userId);
        log.info("Удаляем пользователя с id = {}", userId);
    }
}
