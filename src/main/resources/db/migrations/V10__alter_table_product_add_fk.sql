ALTER TABLE product ADD COLUMN category_id BIGINT NOT NULL;
ALTER TABLE product ADD CONSTRAINT fk_product_company FOREIGN KEY (company_id) REFERENCES company(company_id);
ALTER TABLE product ADD CONSTRAINT fk_product_category FOREIGN KEY (category_id) REFERENCES category(id);