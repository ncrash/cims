CREATE TABLE credit_transactions
(
    id          BIGINT AUTO_INCREMENT NOT NULL,
    created_at  datetime              NOT NULL,
    updated_at  datetime              NOT NULL,
    customer_id BIGINT                NOT NULL,
    type        VARCHAR(20)           NOT NULL,
    amount      DECIMAL               NOT NULL,
    status      VARCHAR(20)           NOT NULL,
    CONSTRAINT pk_credit_transactions PRIMARY KEY (id)
);

CREATE TABLE customers
(
    id                      BIGINT AUTO_INCREMENT NOT NULL,
    created_at              datetime              NOT NULL,
    updated_at              datetime              NOT NULL,
    credit_grade            VARCHAR(10)           NULL,
    credit_grade_updated_at datetime              NULL,
    name                    VARCHAR(50)           NOT NULL,
    birth_date              date                  NOT NULL,
    email                   VARCHAR(100)          NOT NULL,
    phone_number            VARCHAR(20)           NOT NULL,
    CONSTRAINT pk_customers PRIMARY KEY (id)
);

ALTER TABLE credit_transactions
    ADD CONSTRAINT FK_CREDIT_TRANSACTIONS_ON_CUSTOMER FOREIGN KEY (customer_id) REFERENCES customers (id);