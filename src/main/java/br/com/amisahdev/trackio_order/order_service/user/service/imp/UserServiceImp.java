package br.com.amisahdev.trackio_order.order_service.user.service.imp;

import br.com.amisahdev.trackio_order.order_service.geral.exceptions.UserNotFoundException;
import br.com.amisahdev.trackio_order.order_service.user.dto.response.UserResponse;
import br.com.amisahdev.trackio_order.order_service.user.mapper.CompanyMapper;
import br.com.amisahdev.trackio_order.order_service.user.mapper.CustomerMapper;
import br.com.amisahdev.trackio_order.order_service.user.mapper.DeliveryPersonMapper;
import br.com.amisahdev.trackio_order.order_service.user.models.*;
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
    private final CompanyMapper companyMapper;
    private final DeliveryPersonMapper deliveryPersonMapper;
    private final CustomerMapper customerMapper;

    @Transactional
    public Optional<User> findByKeycloakUserId(UUID id) {
        return repository.findByKeycloakUserId(id);
    }

    @Transactional
    public UserResponse getMe(UUID keycloakUserId) {

        User user = repository.findByKeycloakUserId(keycloakUserId)
                .orElseThrow(() -> new UserNotFoundException());

        if (user instanceof Company company) {
            return companyMapper.toResponse(company);
        }

        if (user instanceof DeliveryPerson deliveryPerson) {
            return deliveryPersonMapper.toResponse(deliveryPerson);
        }

        if (user instanceof Customer customer) {
            return customerMapper.toResponse(customer);
        }

        throw new IllegalStateException(
                "Unsupported user type: " + user.getClass().getSimpleName()
        );
    }
}
