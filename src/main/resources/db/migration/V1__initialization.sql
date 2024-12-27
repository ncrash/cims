CREATE TABLE credit_transactions
(
    id          BIGINT AUTO_INCREMENT NOT NULL,
    created_at  datetime              NOT NULL,
    updated_at  datetime              NOT NULL,
    deleted     BIT(1)                NOT NULL,
    deleted_at  datetime              NULL,
    version     INT                   NULL,
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
    deleted                 BIT(1)                NOT NULL,
    deleted_at              datetime              NULL,
    credit_grade            VARCHAR(10)           NULL,
    credit_grade_updated_at datetime              NULL,
    name                    VARCHAR(50)           NOT NULL,
    birth_date              date                  NOT NULL,
    email                   VARCHAR(100)          NOT NULL,
    phone_number            VARCHAR(20)           NOT NULL,
    CONSTRAINT pk_customers PRIMARY KEY (id)
);

CREATE INDEX idx_created_at ON credit_transactions (created_at);

CREATE INDEX idx_credit_grade ON customers (credit_grade);

CREATE INDEX idx_credit_grade_updated_at ON customers (credit_grade_updated_at);

CREATE INDEX idx_status ON credit_transactions (status);

ALTER TABLE credit_transactions
    ADD CONSTRAINT FK_CREDIT_TRANSACTIONS_ON_CUSTOMER FOREIGN KEY (customer_id) REFERENCES customers (id);