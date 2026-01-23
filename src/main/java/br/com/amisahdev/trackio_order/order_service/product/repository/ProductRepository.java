package br.com.amisahdev.trackio_order.order_service.product.repository;

import br.com.amisahdev.trackio_order.order_service.product.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
