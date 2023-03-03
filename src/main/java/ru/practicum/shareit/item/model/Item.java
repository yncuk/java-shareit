package ru.practicum.shareit.item.model;

import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "ITEMS")
@Getter
@Setter
@ToString
public class Item {

    @Id
    @Column(name = "ITEM_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "ITEM_NAME")
    String name;

    String description;

    Boolean available;

    @Column(name = "OWNER_ID")
    int owner;

    @Column(name = "REQUEST_ID")
    int request;

    @OneToMany(mappedBy = "itemId")
    Set<Comment> comments;
}
