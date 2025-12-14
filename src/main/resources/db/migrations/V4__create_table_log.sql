CREATE TABLE order_log (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT,
    text VARCHAR(500) NOT NULL,
    log_date TIMESTAMP NOT NULL,
    CONSTRAINT fk_order_log_order FOREIGN KEY (order_id)
        REFERENCES "order"(id) ON DELETE CASCADE
);

CREATE INDEX idx_order_log_order ON order_log(order_id);

CREATE INDEX idx_order_log_date ON order_log(log_date DESC);

CREATE INDEX idx_order_log_order_date ON order_log(order_id, log_date DESC);

CREATE TABLE product_log (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL,
    action VARCHAR(255) NOT NULL,
    log_date TIMESTAMP NOT NULL,
    CONSTRAINT fk_product_log_product FOREIGN KEY (product_id)
        REFERENCES product(id_product) ON DELETE CASCADE
);

CREATE INDEX idx_product_log_product ON product_log(product_id);

CREATE INDEX idx_product_log_date ON product_log(log_date DESC);

CREATE INDEX idx_product_log_product_date ON product_log(product_id, log_date DESC);

CREATE TABLE user_log (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT,
    action VARCHAR(255) NOT NULL,
    log_date TIMESTAMP NOT NULL,
    CONSTRAINT fk_user_log_user FOREIGN KEY (user_id)
        REFERENCES "user"(user_id) ON DELETE SET NULL
);

CREATE INDEX idx_user_log_user ON user_log(user_id);

CREATE INDEX idx_user_log_date ON user_log(log_date DESC);

CREATE INDEX idx_user_log_user_date ON user_log(user_id, log_date DESC);