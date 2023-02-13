package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Value;
import lombok.With;

@Value
@With
@Builder(toBuilder = true)
public class Item {
    int id;
    String name;
    String description;
    Boolean available;
    int owner;
    int request;
}
