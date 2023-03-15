create table IF NOT EXISTS USERS
(
    USER_ID    serial
        constraint "USERS_pk"
        primary key
        unique,
    USER_NAME  VARCHAR,
    EMAIL      VARCHAR not null,
    IS_DELETED BOOLEAN,
    CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);
create table IF NOT EXISTS ITEMS
(
    ITEM_ID     serial,
    ITEM_NAME   VARCHAR,
    DESCRIPTION VARCHAR,
    AVAILABLE   BOOLEAN,
    OWNER_ID       INTEGER,
    REQUEST_ID     INTEGER,
    constraint "ITEMS_pk"
        primary key (ITEM_ID),
    constraint ITEMS_USERS_USER_ID_FK
        foreign key (OWNER_ID) references USERS
);
create table IF NOT EXISTS booking
(
    booking_id serial
        constraint "BOOKING_pk"
        primary key
        unique,
    start_time TIMESTAMP,
    end_time   TIMESTAMP,
    item_id    INTEGER
        constraint booking_items_item_id_fk
            references items,
    booker_id  INTEGER
        constraint booking_users_user_id_fk
            references users,
    status     VARCHAR(10)
);
create table IF NOT EXISTS comments
(
    comment_id   serial
        constraint "COMMENT_pk"
        primary key
        unique,
    comment_text VARCHAR(150),
    item_id      INTEGER
        constraint comments_items_item_id_fk
            references items,
    author_id    INTEGER
        constraint comments_users_user_id_fk
            references users,
    created      TIMESTAMP
);