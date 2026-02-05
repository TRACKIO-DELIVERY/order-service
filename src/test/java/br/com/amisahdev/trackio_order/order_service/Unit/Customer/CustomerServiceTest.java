package br.com.amisahdev.trackio_order.order_service.Unit.Customer;

import br.com.amisahdev.trackio_order.order_service.geral.exceptions.BusinessException;
import br.com.amisahdev.trackio_order.order_service.geral.exceptions.UserNotFoundException;
import br.com.amisahdev.trackio_order.order_service.security.context.AuthenticatedUser;
import br.com.amisahdev.trackio_order.order_service.security.context.UserContext;
import br.com.amisahdev.trackio_order.order_service.services.AmazonS3Service;
import br.com.amisahdev.trackio_order.order_service.user.dto.request.CustomerRequest;
import br.com.amisahdev.trackio_order.order_service.user.dto.response.CustomerResponse;
import br.com.amisahdev.trackio_order.order_service.user.mapper.AddressMapper;
import br.com.amisahdev.trackio_order.order_service.user.mapper.CustomerMapper;
import br.com.amisahdev.trackio_order.order_service.user.mapper.UserCommonMapper;
import br.com.amisahdev.trackio_order.order_service.user.models.Customer;
import br.com.amisahdev.trackio_order.order_service.user.repository.CustomerRepository;
import br.com.amisahdev.trackio_order.order_service.user.service.imp.CustomerServiceImp;
import br.com.amisahdev.trackio_order.order_service.user.service.imp.UserServiceImp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {

    @Mock private CustomerRepository customerRepository;
    @Mock private CustomerMapper customerMapper;
    @Mock private AddressMapper addressMapper;
    @Mock private AmazonS3Service s3Service;
    @Mock private UserServiceImp userService;
    @Mock private UserContext userContext;
    @Mock private UserCommonMapper userCommonMapper;
    @Mock private MultipartFile image;

    @InjectMocks private CustomerServiceImp customerService;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(customerService, "region", "us-east-1");
        ReflectionTestUtils.setField(customerService, "bucketName", "trackio-bucket");
    }

    @Test
    @DisplayName("1. Deve criar customer com sucesso enviando imagem")
    void create_Success() throws IOException {
        CustomerRequest request = new CustomerRequest();
        request.setCpf("123.456.789-00");

        AuthenticatedUser authUser = new AuthenticatedUser(UUID.randomUUID(), "user", "e@e.com", "Full Name");
        Customer entity = new Customer();

        when(userContext.auth()).thenReturn(authUser);
        when(userService.findByKeycloakUserId(any(UUID.class))).thenReturn(Optional.empty());
        when(customerRepository.existsByCpf(anyString())).thenReturn(false);
        when(customerMapper.toEntity(any())).thenReturn(entity);
        when(s3Service.uploadFile(any(), anyString())).thenReturn("path/key");
        when(customerRepository.save(any())).thenReturn(entity);
        when(customerMapper.toResponse(any())).thenReturn(new CustomerResponse());

        CustomerResponse response = customerService.create(request, image);

        assertNotNull(response);
        verify(customerRepository).save(entity);
    }

    @Test
    @DisplayName("2. Deve lançar BusinessException se o usuário Keycloak já existir")
    void create_UserAlreadyExists() {
        when(userContext.auth()).thenReturn(new AuthenticatedUser(UUID.randomUUID(), "u", "e", "n"));
        when(userService.findByKeycloakUserId(any())).thenReturn(Optional.of(new Customer()));

        assertThrows(BusinessException.class, () -> customerService.create(new CustomerRequest(), null));
    }

    @Test
    @DisplayName("3. Deve lançar RuntimeException se o CPF já existir")
    void create_CpfAlreadyExists() {
        CustomerRequest request = new CustomerRequest();
        request.setCpf("123");
        when(userContext.auth()).thenReturn(new AuthenticatedUser(UUID.randomUUID(), "u", "e", "n"));
        when(userService.findByKeycloakUserId(any())).thenReturn(Optional.empty());
        when(customerRepository.existsByCpf("123")).thenReturn(true);

        assertThrows(RuntimeException.class, () -> customerService.create(request, null));
    }

    @Test
    @DisplayName("4. Rollback: Deve deletar imagem do S3 se salvar no banco falhar")
    void create_RollbackImageOnError() throws IOException {
        Customer entity = new Customer();
        when(userContext.auth()).thenReturn(new AuthenticatedUser(UUID.randomUUID(), "u", "e", "n"));
        when(userService.findByKeycloakUserId(any())).thenReturn(Optional.empty());
        when(customerMapper.toEntity(any())).thenReturn(entity);
        when(s3Service.uploadFile(any(), anyString())).thenReturn("temp-key");
        when(customerRepository.save(any())).thenThrow(new RuntimeException("DB Error"));

        assertThrows(RuntimeException.class, () -> customerService.create(new CustomerRequest(), image));
        verify(s3Service).deleteFile("temp-key");
    }

    @Test
    @DisplayName("5. Update: Deve trocar imagem no S3 com sucesso")
    void update_ShouldRemoveOldImage() throws IOException {
        Long id = 1L;
        Customer existing = new Customer();
        existing.setFileKey("old-key");

        when(customerRepository.findById(id)).thenReturn(Optional.of(existing));
        when(s3Service.uploadFile(any(), anyString())).thenReturn("new-key");
        when(customerRepository.save(any())).thenReturn(existing);

        customerService.update(id, new CustomerRequest(), image);

        verify(s3Service).deleteFile("old-key");
    }

    @Test
    @DisplayName("6. Delete: Deve remover cliente e imagem")
    void delete_Success() {
        Customer entity = new Customer();
        entity.setFileKey("key");
        when(customerRepository.findById(1L)).thenReturn(Optional.of(entity));

        customerService.delete(1L);

        verify(customerRepository).deleteById(1L);
        verify(s3Service).deleteFile("key");
    }

    @Test
    @DisplayName("7. Busca por ID: Deve retornar sucesso")
    void findById_Success() {
        Customer entity = new Customer();
        when(customerRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(customerMapper.toResponse(entity)).thenReturn(new CustomerResponse());

        assertNotNull(customerService.findById(1L));
    }

    @Test
    @DisplayName("8. Busca por CPF: Deve retornar sucesso")
    void findByCpf_Success() {
        Customer entity = new Customer();
        entity.setCpf("123");
        when(customerRepository.findByCpf("123")).thenReturn(Optional.of(entity));
        when(customerMapper.toResponse(entity)).thenReturn(new CustomerResponse());

        assertNotNull(customerService.findByCpf("123"));
    }

    @Test
    @DisplayName("9. Erro: Deve lançar UserNotFoundException quando não encontrar ID")
    void findById_NotFound() {
        when(customerRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> customerService.findById(99L));
    }
}