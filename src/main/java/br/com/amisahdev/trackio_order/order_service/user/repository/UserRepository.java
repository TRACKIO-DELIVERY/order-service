package br.com.amisahdev.trackio_order.order_service.user.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import br.com.amisahdev.trackio_order.order_service.user.models.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByKeycloakUserId(UUID keycloakUserId);

    <T> Optional<T> findOrCreateByKeycloakUserId(UUID keycloakUserId);
}
