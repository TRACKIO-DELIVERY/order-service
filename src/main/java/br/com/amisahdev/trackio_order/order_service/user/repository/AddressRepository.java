package br.com.amisahdev.trackio_order.order_service.user.repository;

import br.com.amisahdev.trackio_order.order_service.user.models.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {
}
