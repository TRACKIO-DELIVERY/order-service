CREATE TABLE "order" (
    id BIGSERIAL PRIMARY KEY,
    company_id BIGINT NOT NULL,
    customer_id BIGINT NOT NULL,
    order_date TIMESTAMP NOT NULL,
    order_amount NUMERIC(10, 2) NOT NULL,
    created_at TIMESTAMP,
    created_user VARCHAR(255) NOT NULL DEFAULT 'Admin',
    updated_at TIMESTAMP,
    updated_user VARCHAR(255) NOT NULL DEFAULT 'Admin',
    CONSTRAINT fk_order_company FOREIGN KEY (company_id)
        REFERENCES company(company_id) ON DELETE CASCADE,
    CONSTRAINT fk_order_customer FOREIGN KEY (customer_id)
        REFERENCES customer(customer_id) ON DELETE CASCADE
);

CREATE INDEX idx_order_company ON "order"(company_id);

CREATE INDEX idx_order_customer ON "order"(customer_id);

CREATE INDEX idx_order_date ON "order"(order_date DESC);

CREATE INDEX idx_order_customer_company ON "order"(customer_id, company_id);

CREATE TABLE order_item (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity DOUBLE PRECISION NOT NULL,
    price NUMERIC(10, 2) NOT NULL,
    CONSTRAINT fk_order_item_order FOREIGN KEY (order_id)
        REFERENCES "order"(id) ON DELETE CASCADE,
    CONSTRAINT fk_order_item_product FOREIGN KEY (product_id)
        REFERENCES product(id_product) ON DELETE RESTRICT
);

CREATE INDEX idx_order_item_order ON order_item(order_id);

CREATE INDEX idx_order_item_product ON order_item(product_id);

ALTER TABLE order_item ADD CONSTRAINT chk_order_item_quantity CHECK (quantity > 0);

ALTER TABLE order_item ADD CONSTRAINT chk_order_item_price CHECK (price >= 0);

CREATE TABLE payment (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL UNIQUE,
    payment_method VARCHAR(50) NOT NULL,
    amount NUMERIC(10, 2) NOT NULL,
    payment_date TIMESTAMP NOT NULL,
    CONSTRAINT fk_payment_order FOREIGN KEY (order_id)
        REFERENCES "order"(id) ON DELETE CASCADE
);

CREATE UNIQUE INDEX idx_payment_order ON payment(order_id);

CREATE INDEX idx_payment_method ON payment(payment_method);

CREATE INDEX idx_payment_date ON payment(payment_date DESC);

ALTER TABLE payment ADD CONSTRAINT chk_payment_amount CHECK (amount > 0);