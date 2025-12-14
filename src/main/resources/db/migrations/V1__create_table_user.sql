CREATE TYPE role AS ENUM ('COMPANY', 'CUSTOMER', 'DELIVERY');

CREATE TABLE "user" (
    user_id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(50) NOT NULL,
    email VARCHAR(50) NOT NULL UNIQUE,
    phone VARCHAR(50) NOT NULL,
    role role,
    expo_push_token VARCHAR(50),
    created_at TIMESTAMP,
    created_user VARCHAR(255) NOT NULL DEFAULT 'Admin',
    updated_at TIMESTAMP,
    updated_user VARCHAR(255) NOT NULL DEFAULT 'Admin'
);

CREATE INDEX idx_user_email ON "user"(email);

CREATE INDEX idx_user_role ON "user"(role);


CREATE TABLE address (
    id_address BIGSERIAL PRIMARY KEY,
    street VARCHAR(200) NOT NULL,
    city VARCHAR(100) NOT NULL,
    state VARCHAR(50) NOT NULL,
    zip_code VARCHAR(8) NOT NULL,
    neighborhood VARCHAR(50) NOT NULL,
    number INTEGER,
    created_at TIMESTAMP,
    created_user VARCHAR(255) NOT NULL DEFAULT 'Admin',
    updated_at TIMESTAMP,
    updated_user VARCHAR(255) NOT NULL DEFAULT 'Admin'
);

CREATE INDEX idx_address_city ON address(city);
CREATE INDEX idx_address_zip_code ON address(zip_code);

CREATE TABLE customer (
    customer_id BIGINT PRIMARY KEY,
    cpf VARCHAR(11) NOT NULL UNIQUE,
    date_of_birth DATE NOT NULL,
    image_url VARCHAR(255),
    CONSTRAINT fk_customer_user FOREIGN KEY (customer_id)
        REFERENCES "user"(user_id) ON DELETE CASCADE
);

CREATE UNIQUE INDEX idx_customer_cpf ON customer(cpf);

CREATE TABLE company (
    company_id BIGINT PRIMARY KEY,
    cnpj VARCHAR(14) NOT NULL UNIQUE,
    bussiness_name VARCHAR(100) NOT NULL,
    image_url VARCHAR(255),
    CONSTRAINT fk_company_user FOREIGN KEY (company_id)
        REFERENCES "user"(user_id) ON DELETE CASCADE
);

CREATE UNIQUE INDEX idx_company_cnpj ON company(cnpj);

CREATE TABLE delivery_person (
    delivery_person_id BIGINT PRIMARY KEY,
    cpf VARCHAR(11) NOT NULL UNIQUE,
    vehicle_type VARCHAR(50),
    image_url VARCHAR(255),
    active BOOLEAN DEFAULT true,
    CONSTRAINT fk_delivery_person_user FOREIGN KEY (delivery_person_id)
        REFERENCES "user"(user_id) ON DELETE CASCADE
);

CREATE INDEX idx_delivery_person_active ON delivery_person(active);

CREATE INDEX idx_delivery_person_cpf ON delivery_person(cpf);