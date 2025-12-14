CREATE TABLE category (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    company_id BIGINT,
    CONSTRAINT fk_category_company FOREIGN KEY (company_id)
        REFERENCES company(company_id) ON DELETE CASCADE
);

CREATE INDEX idx_category_company ON category(company_id);

CREATE UNIQUE INDEX idx_category_name_company ON category(name, company_id);

CREATE TABLE product (
    id_product BIGSERIAL PRIMARY KEY,
    company_id BIGINT NOT NULL,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    price NUMERIC(10, 2) NOT NULL,
    quantity INTEGER NOT NULL DEFAULT 0,
    image_url VARCHAR(255),
    created_at TIMESTAMP,
    created_user VARCHAR(255) NOT NULL DEFAULT 'Admin',
    updated_at TIMESTAMP,
    updated_user VARCHAR(255) NOT NULL DEFAULT 'Admin',
    CONSTRAINT fk_product_company FOREIGN KEY (company_id)
        REFERENCES company(company_id) ON DELETE CASCADE
);

CREATE INDEX idx_product_company ON product(company_id);

CREATE INDEX idx_product_name ON product(name);

CREATE INDEX idx_product_quantity ON product(quantity) WHERE quantity > 0;