package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ItemRequestDtoToCreate {
    int id;
    @NotEmpty
    @NotNull
    String description;
    LocalDateTime created;
}
