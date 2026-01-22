package br.com.amisahdev.trackio_order.order_service.order.Repository;

import br.com.amisahdev.trackio_order.order_service.order.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
