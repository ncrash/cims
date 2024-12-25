drop table if exists customers;

create table customers
(
    birth_date   date,
    credit_grade tinyint check (credit_grade between 0 and 9),
    last_updated date,
    id           bigint not null auto_increment,
    email        varchar(255),
    name         varchar(255),
    phone_number varchar(255),
    primary key (id)
) engine = InnoDB