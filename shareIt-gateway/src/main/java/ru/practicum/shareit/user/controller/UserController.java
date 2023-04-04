package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.UserClient;
import ru.practicum.shareit.user.dto.Create;
import ru.practicum.shareit.user.dto.Update;
import ru.practicum.shareit.user.dto.UserDto;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> findAll() {
        return userClient.findAll();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> findById(@PathVariable long userId) {
        return userClient.findById(userId);
    }

    @PostMapping
    public ResponseEntity<Object> create(@Validated(Create.class) @RequestBody UserDto userDto) {
        return userClient.create(userDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> update(@PathVariable long userId,
                                         @RequestBody @Validated(Update.class) UserDto userDto) {
        return userClient.update(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> delete(@PathVariable long userId) {
        return userClient.delete(userId);
    }
}
