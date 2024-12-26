CREATE TABLE customers
(
    id           BIGINT AUTO_INCREMENT NOT NULL,
    credit_grade VARCHAR(4)            NULL,
    last_updated date                  NULL,
    name         VARCHAR(50)           NOT NULL,
    birth_date   date                  NOT NULL,
    email        VARCHAR(100)          NOT NULL,
    phone_number VARCHAR(20)           NOT NULL,
    CONSTRAINT pk_customers PRIMARY KEY (id)
);