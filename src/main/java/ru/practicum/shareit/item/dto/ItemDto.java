package ru.practicum.shareit.item.dto;

import lombok.*;

@Data
@AllArgsConstructor
public class ItemDto {
    int id;
    String name;
    String description;
    Boolean available;
}
