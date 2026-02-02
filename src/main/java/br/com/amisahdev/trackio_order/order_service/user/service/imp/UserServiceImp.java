package br.com.amisahdev.trackio_order.order_service.user.service.imp;

import br.com.amisahdev.trackio_order.order_service.user.models.User;
import br.com.amisahdev.trackio_order.order_service.user.repository.UserRepository;
import br.com.amisahdev.trackio_order.order_service.user.service.interf.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImp implements UserService {

    private final UserRepository repository;

    @Transactional
    public Optional<User> findByKeycloakUserId(UUID id) {
        return repository.findByKeycloakUserId(id);
    }
}