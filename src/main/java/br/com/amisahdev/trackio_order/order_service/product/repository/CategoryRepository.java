package br.com.amisahdev.trackio_order.order_service.product.repository;

import br.com.amisahdev.trackio_order.order_service.product.model.Category;
import br.com.amisahdev.trackio_order.order_service.user.models.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByNameAndCompany_UserId(String name, Long companyId);
    Optional<Category> findByIdAndCompany_UserId(Long id, Long companyId);
    List<Category> findAllByCompany_UserId(Long companyId);
    @Query("SELECT c FROM Company c WHERE c.userId = :id")
    Optional<Company> findByIdWithDetails(Long id);
}
