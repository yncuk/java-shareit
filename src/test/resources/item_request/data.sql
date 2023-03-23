delete from comments;
delete from booking;
delete from items;
delete from item_request;
delete from users;

insert into users (user_id, user_name, email) values (1, 'user', 'user@user.com');
insert into users (user_id, user_name, email) values (2, 'user2', 'user2@user.com');

insert into item_request (item_request_id, description, requester_id, created) values (2, 'Хочу найти пилу', 1, '2023-03-13T20:01:00');