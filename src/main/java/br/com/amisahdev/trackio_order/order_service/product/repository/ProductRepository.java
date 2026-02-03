package br.com.amisahdev.trackio_order.order_service.product.repository;

import br.com.amisahdev.trackio_order.order_service.product.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategoryId(Long id);
    List<Product> findByCompanyUserId(Long companyId);
    List<Product> findByCategoryIdAndCompanyUserId(Long categoryId, Long companyUserId);
}
