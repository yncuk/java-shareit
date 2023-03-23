delete from comments;
delete from booking;
delete from items;
delete from item_request;
delete from users;

insert into users (user_id, user_name, email) values (1, 'user', 'user@mail.com');
insert into users (user_id, user_name, email) values (2, 'user2', 'user2@mail.com');
insert into users (user_id, user_name, email) values (3, 'user3', 'user3@mail.com');
insert into users (user_id, user_name, email) values (4, 'user4', 'user4@mail.com');
insert into users (user_id, user_name, email) values (5, 'user5', 'user5@mail.com');

insert into items (item_id, item_name, description, available, owner_id) values (1, 'Отвертка', 'Обычная отвертка', true, 1);
insert into items (item_id, item_name, description, available, owner_id) values (2, 'Отвертка2', 'Обычная отвертка2', false, 1);
insert into items (item_id, item_name, description, available, owner_id) values (3, 'Отвертка3', 'Обычная отвертка3', true, 2);
insert into items (item_id, item_name, description, available, owner_id) values (4, 'Отвертка4', 'Обычная отвертка4', true, 2);
insert into items (item_id, item_name, description, available, owner_id) values (5, 'Отвертка5', 'Обычная отвертка5', true, 3);
insert into items (item_id, item_name, description, available, owner_id) values (6, 'Отвертка6', 'Обычная отвертка6', true, 5);

insert into booking (booking_id, start_time, end_time, item_id, booker_id, status) values (10, '2023-03-10T20:01:20', '2023-03-10T21:01:20', 1, 2, 'WAITING');
insert into booking (booking_id, start_time, end_time, item_id, booker_id, status) values (11, '2023-03-10T22:01:20', '2023-03-10T23:01:20', 3, 1, 'APPROVED');
insert into booking (booking_id, start_time, end_time, item_id, booker_id, status) values (12, '2024-03-10T22:01:20', '2024-03-10T23:01:20', 3, 1, 'APPROVED');
insert into booking (booking_id, start_time, end_time, item_id, booker_id, status) values (13, '2023-03-10T22:01:20', '2023-03-10T23:01:20', 3, 1, 'WAITING');
insert into booking (booking_id, start_time, end_time, item_id, booker_id, status) values (14, '2025-03-10T20:01:20', '2025-03-10T21:01:20', 1, 2, 'WAITING');
insert into booking (booking_id, start_time, end_time, item_id, booker_id, status) values (15, '2026-03-10T20:01:20', '2026-03-10T21:01:20', 4, 1, 'WAITING');
insert into booking (booking_id, start_time, end_time, item_id, booker_id, status) values (16, '2020-03-10T20:01:20', '2020-03-10T21:01:20', 3, 3, 'WAITING');
insert into booking (booking_id, start_time, end_time, item_id, booker_id, status) values (17, '2024-03-10T20:01:20', '2024-03-10T21:01:20', 3, 3, 'APPROVED');
insert into booking (booking_id, start_time, end_time, item_id, booker_id, status) values (18, '2020-04-10T20:01:20', '2020-04-10T21:01:20', 3, 4, 'WAITING');
insert into booking (booking_id, start_time, end_time, item_id, booker_id, status) values (19, '2024-04-10T20:01:20', '2024-04-10T21:01:20', 6, 1, 'WAITING');
