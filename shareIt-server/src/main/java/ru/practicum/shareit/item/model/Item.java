package ru.practicum.shareit.item.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "ITEMS")
@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Item {

    @Id
    @Column(name = "ITEM_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @Column(name = "ITEM_NAME")
    String name;

    String description;

    Boolean available;

    @Column(name = "OWNER_ID")
    Integer owner;

    @Column(name = "REQUEST_ID")
    Integer requestId;

    @OneToMany(mappedBy = "itemId")
    Set<Comment> comments;
}
