package br.com.amisahdev.trackio_order.order_service.user.service.interf;

import br.com.amisahdev.trackio_order.order_service.user.models.User;

import java.util.Optional;
import java.util.UUID;

public interface UserService {
    Optional<User> findByKeycloakUserId(UUID id);
}
