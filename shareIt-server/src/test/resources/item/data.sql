delete from comments;
delete from booking;
delete from items;
delete from item_request;
delete from users;

insert into users (user_id, user_name, email) values (1, 'user', 'user@user.com');
insert into users (user_id, user_name, email) values (2, 'user2', 'user2@user.com');
insert into users (user_id, user_name, email) values (3, 'admin', 'admin@admin.com');

insert into items (item_id, item_name, description, available, owner_id) values (3, 'Для обновления', 'Для обновления', true, 1);
insert into items (item_id, item_name, description, available, owner_id) values (4, 'Для обновления available', 'Для обновления available', false, 1);
insert into items (item_id, item_name, description, available, owner_id) values (5, 'Для обновления description', 'Для обновления description', true, 1);
insert into items (item_id, item_name, description, available, owner_id) values (6, 'Для обновления name', 'Для обновления name', true, 1);
insert into items (item_id, item_name, description, available, owner_id) values (7, 'Исключительное слово', 'Исключительное слово', true, 1);
insert into items (item_id, item_name, description, available, owner_id) values (8, 'Для поиска', 'Поиск по описанию', true, 1);
insert into items (item_id, item_name, description, available, owner_id) values (9, 'Пила', 'Пила необычная', true, 1);
