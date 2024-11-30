create table if not exists book(
    id int primary key auto_increment,
    title varchar(300) not null,
    author varchar(300) not null,
    publication year,
    isbn varchar(17) unique,
    copies int not null,
    is_deleted boolean not null default false
);


create table if not exists user(
    id int primary key auto_increment,
    name varchar(300) not null,
    username varchar(300) not null,
    password varchar(70) not null,
    phone_number varchar(25),
    role varchar(6) check ( role in ('ADMIN', 'PATRON') )
);


create table if not exists borrowing(
    id int primary key auto_increment,
    book_id int not null,
    user_id int not null,
    borrowing_date date not null,
    return_date date not null,
    is_returns boolean not null default false,
    foreign key (book_id) references book(id),
    foreign key (user_id) references user(id),
    unique(book_id, user_id, borrowing_date, return_date)
);

insert into book(id, title, author, publication, isbn, copies) values(1, 'dsd', 'sd', 2004, 'sdsds', 5);
insert into user(id, name, username, password, phone_number, role) values(1,'patron', 'admin', 'admin', '0111111', 'ADMIN');
insert into user(id, name, username, password, phone_number, role) values(2,'patron', 'patron', 'patron', '0111111', 'PATRON');
insert into borrowing(book_id, user_id, borrowing_date, return_date) values(1, 1,  '2024-12-1', '2024-01-30');


# select copies from book;
# select count(*) as reserved from borrowing where is_returns = false;
# if copies > reserved: go
# else throw




