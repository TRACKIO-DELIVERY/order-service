package br.com.amisahdev.trackio_order.order_service.Integration.Company;

import br.com.amisahdev.trackio_order.order_service.security.context.AuthenticatedUser;
import br.com.amisahdev.trackio_order.order_service.security.context.UserContext;
import br.com.amisahdev.trackio_order.order_service.services.AmazonS3Service;
import br.com.amisahdev.trackio_order.order_service.user.dto.request.AddressRequest;
import br.com.amisahdev.trackio_order.order_service.user.dto.request.CompanyRequest;
import br.com.amisahdev.trackio_order.order_service.user.dto.response.CompanyResponse;
import br.com.amisahdev.trackio_order.order_service.user.repository.CompanyRepository;
import br.com.amisahdev.trackio_order.order_service.user.service.imp.CompanyServiceImp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@TestPropertySource(properties = {
        "spring.cloud.aws.region.static=us-east-1",
        "aws.s3.bucket=test-bucket"
})
public class CompanyServiceIntegrationTest {

    @Autowired
    private CompanyServiceImp companyService;

    @Autowired
    private CompanyRepository companyRepository;

    @MockitoBean
    private UserContext userContext;

    @MockitoBean
    private AmazonS3Service amazonS3Service;

    private UUID keycloakId;

    @BeforeEach
    void setUp() throws IOException {
        keycloakId = UUID.randomUUID();

        AuthenticatedUser authUser = new AuthenticatedUser(
                keycloakId,
                "usuario.teste",       // username
                "teste@trackio.com",   // email
                "Empresa Teste"
        );

        when(userContext.auth()).thenReturn(authUser);
        when(amazonS3Service.uploadFile(any(), anyString())).thenReturn("mock-file-key.jpg");
    }

    @Test
    @DisplayName("Integração: Deve criar uma Company completa no banco H2 com endereço")
    void integration_CreateCompanyWithAddress() {
        AddressRequest address = new AddressRequest();
        address.setStreet("Rua Teste");
        address.setNumber(123);
        address.setNeighborhood("Bairro");
        address.setCity("Natal");
        address.setState("RN");
        address.setZipCode("59000000");

        CompanyRequest request = new CompanyRequest();
        request.setCnpj("12345678000199");
        request.setBussinessName("Trackio Integration LTDA");
        request.setPhone("8499999999");
        request.setAddress(address);

        CompanyResponse response = companyService.create(request, null);

        assertNotNull(response.getUserId());
        var savedEntity = companyRepository.findById(response.getUserId()).orElseThrow();
        assertEquals(keycloakId, savedEntity.getKeycloakUserId());
        assertEquals("Rua Teste", savedEntity.getAddress().getStreet());
    }
}