package ru.practicum.shareit.user.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> users = new HashMap<>();
    private int id = 1;

    @Override
    public Collection<User> findAll() {
        return users.values().stream().filter(a -> !a.getIsDeleted()).collect(Collectors.toList());
    }

    @Override
    public User findById(Integer id) {
        if (users.containsKey(id)) {
            if (!users.get(id).getIsDeleted()) {
                return users.get(id);
            } else throw new EntityNotFoundException("Не найден пользователь");
        } else throw new EntityNotFoundException("Не найден пользователь");
    }

    @Override
    public User create(User user) {
        user.setId(id);
        user.setIsDeleted(false);
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
    public void softDelete(Integer userId) {
        users.get(userId).setIsDeleted(true);
        log.info("Удаляем пользователя с id = {}", userId);
    }
}
