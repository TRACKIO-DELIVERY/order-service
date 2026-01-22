package br.com.amisahdev.trackio_order.order_service.user.repository;

import br.com.amisahdev.trackio_order.order_service.user.models.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long>
{
    boolean existsByCpf(String cpf);
    Optional<Customer> findByCpf(String cpf);
}
