package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Value;
import lombok.With;

@Value
@Builder(toBuilder = true)
public class Item {
    @With
    int id;
    String name;
    String description;
    @With
    Boolean available;
    int owner;
    int request;
}
