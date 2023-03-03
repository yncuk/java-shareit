package ru.practicum.shareit.user.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "USERS")
@Getter
@Setter
@ToString
public class User {
    @Id
    @Column(name = "USER_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "USER_NAME")
    String name;

    @NotBlank @Email
    String email;

    @Column(name = "IS_DELETED")
    Boolean isDeleted;

}
