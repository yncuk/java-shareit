package ru.practicum.shareit.user;

import lombok.Builder;
import lombok.Value;
import lombok.With;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Value
@Builder(toBuilder = true)
public class User {
    @With
    int id;
    @With
    String name;
    @With
    @NotBlank @Email
    String email;
}
