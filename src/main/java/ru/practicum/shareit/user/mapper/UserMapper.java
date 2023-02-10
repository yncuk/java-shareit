package ru.practicum.shareit.user.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.ArrayList;
import java.util.Collection;

@Component
public class UserMapper {
    public static UserDto toUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail());
    }

    public static Collection<UserDto> allToUserDto(Collection<User> users) {
        Collection<UserDto> returnUsers = new ArrayList<>();
        for (User currentUser : users) {
            returnUsers.add(toUserDto(currentUser));
        }
        return returnUsers;
    }
}
