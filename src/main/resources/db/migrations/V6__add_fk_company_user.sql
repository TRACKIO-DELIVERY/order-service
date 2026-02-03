ALTER TABLE company
ADD CONSTRAINT fk_company_user
FOREIGN KEY (company_id)
REFERENCES "user"(user_id);
