package br.com.amisahdev.trackio_order.order_service.user.repository;

import br.com.amisahdev.trackio_order.order_service.user.models.DeliveryPerson;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryPersonRepository extends JpaRepository<DeliveryPerson, Long> {
    boolean existsByCpf(String cpf);
    DeliveryPerson findByCpf(String cpf);
}
