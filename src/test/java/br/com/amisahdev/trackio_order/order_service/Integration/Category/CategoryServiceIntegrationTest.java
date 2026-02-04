package br.com.amisahdev.trackio_order.order_service.Integration.Category;

import br.com.amisahdev.trackio_order.order_service.product.dto.request.CategoryRequest;
import br.com.amisahdev.trackio_order.order_service.product.dto.response.CategoryResponse;
import br.com.amisahdev.trackio_order.order_service.product.repository.CategoryRepository;
import br.com.amisahdev.trackio_order.order_service.product.service.imp.CategoryServiceImp;
import br.com.amisahdev.trackio_order.order_service.user.models.Company;
import br.com.amisahdev.trackio_order.order_service.user.repository.CompanyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@TestPropertySource(properties = {
        "BUCKET_NAME=test-bucket",
        "aws.region=us-east-1",
        "aws.accessKey=fake",
        "aws.secretKey=fake"
})
public class CategoryServiceIntegrationTest {

    @Autowired private CategoryServiceImp categoryService;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private CompanyRepository companyRepository;

    private Company savedCompany;

    @BeforeEach
    void setUp() {
        categoryRepository.deleteAll();
        companyRepository.deleteAll();

        Company company = Company.builder()
                .bussinessName("Tech Store")
                .cnpj("12345678000199")
                .email("admin@techstore.com")
                .username("admin_tech")
                .keycloakUserId(UUID.randomUUID())
                .expoPushToken("123456")
                .phone("84999343899")
                .fullname("Tech Store Admin")
                .build();

        savedCompany = companyRepository.save(company);
    }

    @Test
    @DisplayName("Integração: Deve persistir e recuperar uma categoria do banco H2")
    void integration_CreateAndFindCategory() {
        CategoryRequest request = new CategoryRequest();
        request.setName("Eletrônicos");
        request.setCompanyId(savedCompany.getUserId());

        CategoryResponse savedResponse = categoryService.create(request);
        CategoryResponse foundResponse = categoryService.findById(savedResponse.getId());

        assertNotNull(foundResponse);
        assertEquals("Eletrônicos", foundResponse.getName());
        assertTrue(categoryRepository.existsById(savedResponse.getId()));
    }

    @Test
    @DisplayName("Integração: Deve impedir nomes duplicados para a mesma empresa no banco")
    void integration_ShouldPreventDuplicateName() {
        CategoryRequest request = new CategoryRequest();
        request.setName("Hardware");
        request.setCompanyId(savedCompany.getUserId());
        categoryService.create(request);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            categoryService.create(request);
        });

        assertEquals("Category already exists", ex.getMessage());
    }
}