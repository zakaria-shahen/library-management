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
    role varchar(6) check ( role in ('ADMIN', 'PATRON') ),
    is_deleted boolean not null default false

);


create table if not exists borrowing(
    id int primary key auto_increment,
    book_id int not null,
    user_id int not null,
    borrowing_date date not null,
    return_date date not null,
    is_returned boolean not null default false,
    is_deleted boolean not null default false,
    foreign key (book_id) references book(id),
    foreign key (user_id) references user(id),
    unique(book_id, user_id, borrowing_date, return_date)
);
