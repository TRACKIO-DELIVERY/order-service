package br.com.amisahdev.trackio_order.order_service.user.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import br.com.amisahdev.trackio_order.order_service.user.models.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
