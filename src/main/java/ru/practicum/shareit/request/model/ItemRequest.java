package ru.practicum.shareit.request.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@Entity
@Table(name = "ITEM_REQUEST")
@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemRequest {

    @Id
    @Column(name = "ITEM_REQUEST_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    String description;

    @ManyToOne
    @JoinColumn(name = "REQUESTER_ID", referencedColumnName = "USER_ID")
    User requester;

    LocalDateTime created;

    @ManyToMany(mappedBy = "requestId")
    List<Item> items;
}
