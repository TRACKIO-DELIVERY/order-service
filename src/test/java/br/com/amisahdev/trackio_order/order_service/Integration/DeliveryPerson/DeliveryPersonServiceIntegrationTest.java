package br.com.amisahdev.trackio_order.order_service.Integration.DeliveryPerson;

import br.com.amisahdev.trackio_order.order_service.security.context.AuthenticatedUser;
import br.com.amisahdev.trackio_order.order_service.security.context.UserContext;
import br.com.amisahdev.trackio_order.order_service.user.dto.request.DeliveryPersonRequest;
import br.com.amisahdev.trackio_order.order_service.user.dto.response.DeliveryPersonResponse;
import br.com.amisahdev.trackio_order.order_service.user.repository.DeliveryPersonRepository;
import br.com.amisahdev.trackio_order.order_service.user.service.imp.DeliveryPersonServiceImp;
import br.com.amisahdev.trackio_order.order_service.user.service.imp.UserServiceImp;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@TestPropertySource(properties = {
        "spring.cloud.aws.region.static=us-east-1",
        "aws.s3.bucket=test-bucket",
        "spring.autoconfigure.exclude=org.springframework.cloud.aws.autoconfigure.context.ContextInstanceDataAutoConfiguration"
})
public class DeliveryPersonServiceIntegrationTest {

    @Autowired private DeliveryPersonServiceImp deliveryPersonService;
    @Autowired private DeliveryPersonRepository deliveryPersonRepository;

    @MockitoBean private UserContext userContext;

    @MockitoBean private UserServiceImp userService;

    @Test
    @DisplayName("Integração: Deve persistir DeliveryPerson no H2")
    void integration_CreateDeliveryPerson() {
        UUID keycloakId = UUID.randomUUID();
        AuthenticatedUser authUser = new AuthenticatedUser(
                keycloakId,
                "driver.01",
                "driver@test.com",
                "John Driver"
        );

        when(userContext.auth()).thenReturn(authUser);
        when(userService.findByKeycloakUserId(any(UUID.class))).thenReturn(Optional.empty());

        DeliveryPersonRequest request = new DeliveryPersonRequest();
        request.setCpf("999.888.777-66");

        DeliveryPersonResponse response = deliveryPersonService.create(request);

        assertNotNull(response.getUserId());
        var saved = deliveryPersonRepository.findById(response.getUserId()).get();
        assertEquals("John Driver", saved.getFullname());
    }
}