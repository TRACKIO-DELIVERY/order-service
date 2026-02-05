package br.com.amisahdev.trackio_order.order_service.Unit.DeliveryPerson;


import br.com.amisahdev.trackio_order.order_service.geral.exceptions.BusinessException;
import br.com.amisahdev.trackio_order.order_service.geral.exceptions.UserNotFoundException;
import br.com.amisahdev.trackio_order.order_service.security.context.AuthenticatedUser;
import br.com.amisahdev.trackio_order.order_service.security.context.UserContext;
import br.com.amisahdev.trackio_order.order_service.user.dto.request.DeliveryPersonRequest;
import br.com.amisahdev.trackio_order.order_service.user.dto.response.DeliveryPersonResponse;
import br.com.amisahdev.trackio_order.order_service.user.mapper.DeliveryPersonMapper;
import br.com.amisahdev.trackio_order.order_service.user.mapper.UserCommonMapper;
import br.com.amisahdev.trackio_order.order_service.user.models.DeliveryPerson;
import br.com.amisahdev.trackio_order.order_service.user.repository.CustomerRepository;
import br.com.amisahdev.trackio_order.order_service.user.repository.DeliveryPersonRepository;
import br.com.amisahdev.trackio_order.order_service.user.service.imp.DeliveryPersonServiceImp;
import br.com.amisahdev.trackio_order.order_service.user.service.interf.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DeliveryPersonServiceTest {

    @Mock private DeliveryPersonRepository deliveryPersonRepository;
    @Mock private DeliveryPersonMapper deliveryPersonMapper;
    @Mock private CustomerRepository customerRepository;
    @Mock private UserContext userContext;
    @Mock private UserService userService;
    @Mock private UserCommonMapper userCommonMapper;

    @InjectMocks private DeliveryPersonServiceImp deliveryPersonService;

    @Test
    @DisplayName("1. Deve criar entregador com sucesso")
    void create_Success() {
        DeliveryPersonRequest request = new DeliveryPersonRequest();
        request.setCpf("111.222.333-44");

        AuthenticatedUser authUser = new AuthenticatedUser(UUID.randomUUID(), "delivery.man", "d@test.com", "Delivery Person");
        DeliveryPerson entity = new DeliveryPerson();

        when(userContext.auth()).thenReturn(authUser);
        when(userService.findByKeycloakUserId(any(UUID.class))).thenReturn(Optional.empty());
        when(deliveryPersonRepository.existsByCpf(anyString())).thenReturn(false);
        when(customerRepository.existsByCpf(anyString())).thenReturn(false);
        when(deliveryPersonMapper.toEntity(any())).thenReturn(entity);
        when(deliveryPersonRepository.save(any())).thenReturn(entity);
        when(deliveryPersonMapper.toResponse(any())).thenReturn(new DeliveryPersonResponse());

        DeliveryPersonResponse response = deliveryPersonService.create(request);

        assertNotNull(response);
        verify(deliveryPersonRepository).save(entity);
    }

    @Test
    @DisplayName("2. Deve lançar BusinessException se o usuário Keycloak já existir")
    void create_UserAlreadyExists() {
        AuthenticatedUser authUser = new AuthenticatedUser(UUID.randomUUID(), "u", "e", "n");
        when(userContext.auth()).thenReturn(authUser);

        when(userService.findByKeycloakUserId(any(UUID.class))).thenReturn(Optional.of(new DeliveryPerson()));

        assertThrows(BusinessException.class, () -> deliveryPersonService.create(new DeliveryPersonRequest()));

        verify(deliveryPersonRepository, never()).save(any());
    }

    @Test
    @DisplayName("3. Deve lançar erro se CPF já existir em DeliveryPerson")
    void create_CpfExistsInDelivery() {
        DeliveryPersonRequest request = new DeliveryPersonRequest();
        request.setCpf("123");
        when(userContext.auth()).thenReturn(new AuthenticatedUser(UUID.randomUUID(), "u", "e", "n"));
        when(userService.findByKeycloakUserId(any())).thenReturn(Optional.empty());
        when(deliveryPersonRepository.existsByCpf("123")).thenReturn(true);

        assertThrows(RuntimeException.class, () -> deliveryPersonService.create(request));
    }

    @Test
    @DisplayName("4. Deve lançar erro se CPF já existir em Customer")
    void create_CpfExistsInCustomer() {
        DeliveryPersonRequest request = new DeliveryPersonRequest();
        request.setCpf("123");
        when(userContext.auth()).thenReturn(new AuthenticatedUser(UUID.randomUUID(), "u", "e", "n"));
        when(userService.findByKeycloakUserId(any())).thenReturn(Optional.empty());
        when(deliveryPersonRepository.existsByCpf("123")).thenReturn(false);
        when(customerRepository.existsByCpf("123")).thenReturn(true);

        assertThrows(RuntimeException.class, () -> deliveryPersonService.create(request));
    }

    @Test
    @DisplayName("5. Deve atualizar entregador com sucesso")
    void update_Success() {
        Long id = 1L;
        DeliveryPerson entity = new DeliveryPerson();
        when(deliveryPersonRepository.findById(id)).thenReturn(Optional.of(entity));
        when(deliveryPersonRepository.save(any())).thenReturn(entity);
        when(deliveryPersonMapper.toResponse(any())).thenReturn(new DeliveryPersonResponse());

        deliveryPersonService.update(id, new DeliveryPersonRequest());

        verify(deliveryPersonRepository).save(entity);
    }

    @Test
    @DisplayName("6. Deve deletar entregador com sucesso")
    void delete_Success() {
        when(deliveryPersonRepository.findById(1L)).thenReturn(Optional.of(new DeliveryPerson()));
        deliveryPersonService.delete(1L);
        verify(deliveryPersonRepository).deleteById(1L);
    }

    @Test
    @DisplayName("7. Deve buscar por ID com sucesso")
    void findById_Success() {
        DeliveryPerson entity = new DeliveryPerson();
        when(deliveryPersonRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(deliveryPersonMapper.toResponse(entity)).thenReturn(new DeliveryPersonResponse());

        assertNotNull(deliveryPersonService.findById(1L));
    }

    @Test
    @DisplayName("8. Deve buscar por CPF com sucesso")
    void findByCpf_Success() {
        DeliveryPerson entity = new DeliveryPerson();
        when(deliveryPersonRepository.findByCpf(anyString())).thenReturn(Optional.of(entity));
        when(deliveryPersonMapper.toResponse(entity)).thenReturn(new DeliveryPersonResponse());

        assertNotNull(deliveryPersonService.findByCpf("123"));
    }

    @Test
    @DisplayName("9. Deve lançar UserNotFoundException")
    void findById_NotFound() {
        when(deliveryPersonRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> deliveryPersonService.findById(99L));
    }
}