ALTER TABLE customers
    ADD username VARCHAR(20) NOT NULL after id;

ALTER TABLE customers
    ADD password VARCHAR(255) NOT NULL after username;

ALTER TABLE customers
    ADD CONSTRAINT uc_customers_username UNIQUE (username);
