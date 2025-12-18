ALTER TABLE customer
    ADD COLUMN address_id BIGINT;

ALTER TABLE customer
    ADD CONSTRAINT fk_customer_address
    FOREIGN KEY (address_id)
    REFERENCES address(id_address);

ALTER TABLE company
        ADD COLUMN address_id BIGINT;


ALTER TABLE company
    ADD CONSTRAINT fk_company_address
    FOREIGN KEY (address_id)
    REFERENCES address(id_address);