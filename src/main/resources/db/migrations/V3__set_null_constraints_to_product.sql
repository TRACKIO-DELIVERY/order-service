UPDATE product SET name = 'Unnamed' WHERE name IS NULL;
UPDATE product SET stock = 0 WHERE stock IS NULL;
UPDATE product SET price = 0 WHERE price IS NULL;


ALTER TABLE product
    ALTER COLUMN name SET NOT NULL;

ALTER TABLE product
    ALTER COLUMN stock SET NOT NULL;

ALTER TABLE product
    ALTER COLUMN price SET NOT NULL;

