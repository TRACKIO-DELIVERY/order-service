package br.com.amisahdev.trackio_order.order_service.order.repository;

import br.com.amisahdev.trackio_order.order_service.order.model.Order;
import br.com.amisahdev.trackio_order.order_service.order.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByOrderStatus(OrderStatus orderStatus);
}
