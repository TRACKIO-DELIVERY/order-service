alter table "order" add column delivery_id BIGINT;
ALTER TABLE "order" ADD CONSTRAINT fk_order_delivery FOREIGN KEY (delivery_id) REFERENCES delivery_person(delivery_person_id)