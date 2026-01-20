package br.com.amisahdev.trackio_order.order_service.user.services;

import br.com.amisahdev.trackio_order.order_service.user.dto.UserKeycloakDto;
import br.com.amisahdev.trackio_order.order_service.user.mapper.UserMapper;
import br.com.amisahdev.trackio_order.order_service.user.models.User;
import br.com.amisahdev.trackio_order.order_service.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository repository;
    private final UserMapper mapper;

    public UserService(UserRepository repository, UserMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Transactional
    public User findOrCreate(UserKeycloakDto dto) {

        return repository.findByKeycloakUserId(dto.getKeycloakUserId())
                .orElseGet(() ->
                        repository.save(mapper.fromKeycloak(dto))
                );
    }
}
