package ru.practicum.shareit.user.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityNotFoundException;
import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public Collection<UserDto> findAll() {
        return UserMapper.allToUserDto(userRepository.findAll());
    }

    @Override
    public UserDto findById(Integer id) {
        return UserMapper.toUserDto(userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Не найден пользователь")));
    }

    @Override
    public UserDto create(UserDto userDto) {
        return UserMapper.toUserDto(userRepository.save(UserMapper.toUser(userDto)));
    }

    @Override
    public UserDto update(Integer userId, UserDto userDto) {
        User newUser = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Не найден пользователь"));
        if (userDto.getEmail() != null) {
            newUser.setEmail(userDto.getEmail());
            log.info("Задаем новую почту пользователю - {}", userDto.getEmail());
        }
        if (userDto.getName() != null) {
            newUser.setName(userDto.getName());
            log.info("Задаем новое имя пользователю - {}", userDto.getName());
        }
        return UserMapper.toUserDto(userRepository.save(newUser));
    }

    @Override
    public void delete(Integer userId) {
        userRepository.deleteById(userId);
    }
}
