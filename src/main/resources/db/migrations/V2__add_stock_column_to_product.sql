ALTER TABLE product
    ADD COLUMN stock INT;

UPDATE product SET stock = 0 WHERE stock IS NULL;
