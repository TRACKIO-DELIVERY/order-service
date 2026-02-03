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
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class CategoryServiceIntegrationTest {

    @Autowired private CategoryServiceImp categoryService;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private CompanyRepository companyRepository;

    private Company savedCompany;

    @BeforeEach
    void setUp() {
        categoryRepository.deleteAll();
        companyRepository.deleteAll();


        Company company = new Company();
        company.setBussinessName("Tech Store");
        company.setCnpj("12345678000199");
        company.setEmail("admin@techstore.com");
        company.setUsername("admin_tech");
        company.setPassword("secure123");
        company.setExpoPushToken("123456");
        company.setPhone("84999343899");

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
