package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public Collection<UserDto> findAll() {
        return userService.findAll();
    }

    @GetMapping("/{userId}")
    public UserDto findById(@PathVariable Integer userId) {
        return userService.findById(userId);
    }

    @PostMapping
    public UserDto create(@RequestBody UserDto userDto) {
        return userService.create(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDto update(@PathVariable Integer userId,
                          @RequestBody UserDto userDto) {
        return userService.update(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable Integer userId) {
        userService.delete(userId);
    }
}
