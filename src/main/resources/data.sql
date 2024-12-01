insert into book(id, title, author, publication, isbn, copies) values(1, 'dsd', 'sd', 2004, 'sdsds', 5);
insert into book(id, title, author, publication, isbn, copies) values(2, 'sssdsd', 'sd', 2004, 'dddsdsds', 5);
insert into user(id, name, username, password, phone_number, role) values(1,'Admin', 'admin', '{noop}admin', '0111111', 'ADMIN');
insert into user(id, name, username, password, phone_number, role) values(3,'admin 2', 'admin2', '{bcrypt}$2a$10$LpNSxmcJi6eAtms.83dKC.Ick82wyyRzwWxXYlqxv1OUX8hJd.rVG', '0111111', 'ADMIN');
insert into user(id, name, username, password, phone_number, role) values(2,'patron', 'patron', '{noop}patron', '0111111', 'PATRON');
insert into borrowing(book_id, user_id, borrowing_date, return_date) values(1, 1,  '2024-12-1', '2024-01-30');

