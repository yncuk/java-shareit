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
    String name;
    @NotBlank @Email
    String email;
    @With
    Boolean isDeleted;
}
