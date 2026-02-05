package br.com.amisahdev.trackio_order.order_service.Integration.Customer;

import br.com.amisahdev.trackio_order.order_service.security.context.AuthenticatedUser;
import br.com.amisahdev.trackio_order.order_service.security.context.UserContext;
import br.com.amisahdev.trackio_order.order_service.services.AmazonS3Service;
import br.com.amisahdev.trackio_order.order_service.user.dto.request.AddressRequest;
import br.com.amisahdev.trackio_order.order_service.user.dto.request.CustomerRequest;
import br.com.amisahdev.trackio_order.order_service.user.dto.response.CustomerResponse;
import br.com.amisahdev.trackio_order.order_service.user.repository.CustomerRepository;
import br.com.amisahdev.trackio_order.order_service.user.service.imp.CustomerServiceImp;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@TestPropertySource(properties = {
        "spring.cloud.aws.region.static=us-east-1",
        "aws.s3.bucket=test-bucket"
})
class CustomerServiceIntegrationTest {

    @Autowired
    private CustomerServiceImp customerService;
    @Autowired private CustomerRepository customerRepository;

    @MockitoBean
    private UserContext userContext;
    @MockitoBean private AmazonS3Service s3Service;

    @Test
    @DisplayName("Integração: Deve persistir Customer e Address no H2")
    void integration_CreateCustomer() {
        UUID keycloakId = UUID.randomUUID();
        AuthenticatedUser authUser = new AuthenticatedUser(keycloakId, "cust.test", "cust@test.com", "Customer Test");
        when(userContext.auth()).thenReturn(authUser);

        AddressRequest address = new AddressRequest();
        address.setStreet("Rua Teste");
        address.setNumber(123);
        address.setNeighborhood("Bairro");
        address.setCity("Natal");
        address.setState("RN");
        address.setZipCode("59000000");

        CustomerRequest request = new CustomerRequest();
        request.setCpf("12345678901");
        request.setDateOfBirth(LocalDate.of(2003, 2, 27));
        request.setPhone("84999586523");
        request.setExpoPushToken(UUID.randomUUID().toString());
        request.setAddress(address);

        CustomerResponse response = customerService.create(request, null);

        assertNotNull(response.getUserId());
        var saved = customerRepository.findById(response.getUserId()).get();
        assertEquals("Customer Test", saved.getFullname());
        assertEquals(keycloakId, saved.getKeycloakUserId());
    }
}