package ru.practicum.shareit.item.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemRequestDto {
    long id;
    @NotBlank
    String name;
    @NotBlank
    String description;
    @NotNull
    Boolean available;
    Integer requestId;
}
