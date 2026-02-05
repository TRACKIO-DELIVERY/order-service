package br.com.amisahdev.trackio_order.order_service.Unit.Company;

import br.com.amisahdev.trackio_order.order_service.geral.exceptions.CnpjAlreadyExistsException;
import br.com.amisahdev.trackio_order.order_service.geral.exceptions.UserAlreadyExistsException;
import br.com.amisahdev.trackio_order.order_service.geral.exceptions.UserNotFoundException;
import br.com.amisahdev.trackio_order.order_service.security.context.AuthenticatedUser;
import br.com.amisahdev.trackio_order.order_service.security.context.UserContext;
import br.com.amisahdev.trackio_order.order_service.services.AmazonS3Service;
import br.com.amisahdev.trackio_order.order_service.user.dto.request.CompanyRequest;
import br.com.amisahdev.trackio_order.order_service.user.dto.response.CompanyResponse;
import br.com.amisahdev.trackio_order.order_service.user.mapper.AddressMapper;
import br.com.amisahdev.trackio_order.order_service.user.mapper.CompanyMapper;
import br.com.amisahdev.trackio_order.order_service.user.mapper.UserCommonMapper;
import br.com.amisahdev.trackio_order.order_service.user.models.Company;
import br.com.amisahdev.trackio_order.order_service.user.repository.CompanyRepository;
import br.com.amisahdev.trackio_order.order_service.user.service.imp.CompanyServiceImp;
import br.com.amisahdev.trackio_order.order_service.user.service.interf.UserService;
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
public class CompanyServiceTest {

    @Mock private CompanyRepository companyRepository;
    @Mock private CompanyMapper companyMapper;
    @Mock private AddressMapper addressMapper;
    @Mock private AmazonS3Service amazonS3Service;
    @Mock private UserContext userContext;
    @Mock private UserService userService;
    @Mock private UserCommonMapper userCommonMapper;
    @Mock private MultipartFile image;

    @InjectMocks private CompanyServiceImp companyService;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(companyService, "region", "us-east-1");
        ReflectionTestUtils.setField(companyService, "bucketName", "trackio-bucket");
    }

    @Test
    @DisplayName("Deve criar empresa com sucesso enviando imagem")
    void create_Success() throws IOException {
        CompanyRequest request = new CompanyRequest();
        request.setCnpj("12345678000199");

        UUID mockKeycloakId = UUID.randomUUID();
        AuthenticatedUser authUser = new AuthenticatedUser(mockKeycloakId, "user", "email@test.com", null);
        Company companyEntity = Company.builder().build();

        when(userContext.auth()).thenReturn(authUser);

        when(userService.findByKeycloakUserId(any(UUID.class))).thenReturn(Optional.empty());

        when(companyRepository.existsByCnpj(anyString())).thenReturn(false);
        when(companyMapper.toEntity(any())).thenReturn(companyEntity);
        when(amazonS3Service.uploadFile(any(), anyString())).thenReturn("path/to/image.jpg");
        when(companyRepository.save(any())).thenReturn(companyEntity);
        when(companyMapper.toResponse(any())).thenReturn(new CompanyResponse());

        CompanyResponse response = companyService.create(request, image);

        assertNotNull(response);
        verify(companyRepository).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção se CNPJ já existir")
    void create_CnpjAlreadyExists() {
        CompanyRequest request = new CompanyRequest();
        request.setCnpj("123456");
        AuthenticatedUser authUser = new AuthenticatedUser(UUID.randomUUID(), "u", "e", null);

        when(userContext.auth()).thenReturn(authUser);
        when(userService.findByKeycloakUserId(any())).thenReturn(Optional.empty());
        when(companyRepository.existsByCnpj("123456")).thenReturn(true);

        assertThrows(CnpjAlreadyExistsException.class, () -> companyService.create(request, null));
    }

    @Test
    @DisplayName("Rollback: Deve deletar imagem do S3 se salvar no banco falhar")
    void create_RollbackImageOnDatabaseError() throws IOException {
        CompanyRequest request = new CompanyRequest();
        Company companyEntity = Company.builder().build();

        when(userContext.auth()).thenReturn(new AuthenticatedUser(UUID.randomUUID(), "u", "e", null));
        when(userService.findByKeycloakUserId(any())).thenReturn(Optional.empty());
        when(companyMapper.toEntity(any())).thenReturn(companyEntity);
        when(amazonS3Service.uploadFile(any(), anyString())).thenReturn("temp-key");

        when(companyRepository.save(any())).thenThrow(new RuntimeException("DB Error"));

        assertThrows(RuntimeException.class, () -> companyService.create(request, image));

        verify(amazonS3Service).deleteFile("temp-key");
    }

    @Test
    @DisplayName("Update: Deve remover imagem antiga do S3 ao subir uma nova")
    void update_ShouldRemoveOldImage() throws IOException {
        Long id = 1L;
        Company existingCompany = Company.builder()
                .fileKey("old-key")
                .build();

        when(companyRepository.findById(id)).thenReturn(Optional.of(existingCompany));
        when(amazonS3Service.uploadFile(any(), anyString())).thenReturn("new-key");
        when(companyRepository.save(any())).thenReturn(existingCompany);

        companyService.update(id, new CompanyRequest(), image);

        verify(amazonS3Service).uploadFile(image, "Company");
        verify(amazonS3Service).deleteFile("old-key");
    }

    @Test
    @DisplayName("Delete: Deve remover empresa e arquivo do S3")
    void delete_Success() {
        Company company = Company.builder().fileKey("delete-me").build();
        when(companyRepository.findById(1L)).thenReturn(Optional.of(company));

        companyService.delete(1L);

        verify(companyRepository).deleteById(1L);
        verify(amazonS3Service).deleteFile("delete-me");
    }


    @Test
    @DisplayName("Cenário 2: Deve lançar erro se o usuário já existir no Keycloak")
    void create_ShouldThrowIfUserAlreadyExists() {
        AuthenticatedUser authUser = new AuthenticatedUser(UUID.randomUUID(), "user", "e@e.com", null);

        when(userContext.auth()).thenReturn(authUser);

        Company existingCompany = Company.builder()
                .bussinessName("Empresa Já Existente")
                .build();

        when(userService.findByKeycloakUserId(any(UUID.class))).thenReturn(Optional.of(existingCompany));

        assertThrows(UserAlreadyExistsException.class, () -> companyService.create(new CompanyRequest(), null));

        verify(companyRepository, never()).save(any());
    }

    @Test
    @DisplayName("Cenário 8 (A): Deve buscar empresa por ID com sucesso")
    void findById_Success() {
        Long id = 1L;
        Company entity = Company.builder().userId(id).build();

        when(companyRepository.findById(id)).thenReturn(Optional.of(entity));
        when(companyMapper.toResponse(entity)).thenReturn(new CompanyResponse());

        CompanyResponse response = companyService.findById(id);

        assertNotNull(response);
        verify(companyRepository).findById(id);
    }

    @Test
    @DisplayName("Cenário 8 (B): Deve buscar empresa por CNPJ com sucesso")
    void findByCnpj_Success() {
        String cnpj = "12345678000199";
        Company entity = Company.builder().cnpj(cnpj).build();

        when(companyRepository.findByCnpj(cnpj)).thenReturn(Optional.of(entity));
        when(companyMapper.toResponse(entity)).thenReturn(new CompanyResponse());

        CompanyResponse response = companyService.findByCnpj(cnpj);

        assertNotNull(response);
        assertEquals(cnpj, entity.getCnpj());
    }

    @Test
    @DisplayName("Cenário Adicional: Deve lançar UserNotFoundException ao buscar ID inexistente")
    void findById_NotFound_ThrowsException() {
        when(companyRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> companyService.findById(99L));
    }
}