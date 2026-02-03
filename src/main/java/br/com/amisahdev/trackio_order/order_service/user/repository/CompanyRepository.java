package br.com.amisahdev.trackio_order.order_service.user.repository;

import br.com.amisahdev.trackio_order.order_service.user.models.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    boolean existsByCnpj(String cnpj);
    Optional<Company> findByCnpj(String cnpj);
}
