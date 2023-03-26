delete from comments;
delete from booking;
delete from items;
delete from item_request;
delete from users;

insert into users (user_id, user_name, email) values (4, 'user2', 'user2@user2.com');
insert into users (user_id, user_name, email) values (5, 'user_duplicate', 'user_duplicate@mail.ru');
insert into users (user_id, user_name, email) values (6, 'exist', 'exist_email@mail.ru');
insert into users (user_id, user_name, email) values (7, 'for_update', 'for_update@mail.ru');
insert into users (user_id, user_name, email) values (8, 'for_delete', 'for_delete@mail.ru');