package br.com.amisahdev.trackio_order.order_service.Integration.Product;

import br.com.amisahdev.trackio_order.order_service.product.dto.request.ProductRequest;
import br.com.amisahdev.trackio_order.order_service.product.dto.response.ProductResponse;
import br.com.amisahdev.trackio_order.order_service.product.model.Category;
import br.com.amisahdev.trackio_order.order_service.product.model.Product;
import br.com.amisahdev.trackio_order.order_service.product.repository.CategoryRepository;
import br.com.amisahdev.trackio_order.order_service.product.repository.ProductRepository;
import br.com.amisahdev.trackio_order.order_service.product.service.imp.ProductServiceImp;
import br.com.amisahdev.trackio_order.order_service.user.models.Company;
import br.com.amisahdev.trackio_order.order_service.user.repository.CompanyRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ProductServiceIntegrationTest {
    @Autowired
    private ProductServiceImp productService;
    @Autowired private ProductRepository productRepository;
    @Autowired private CompanyRepository companyRepository;
    @Autowired private CategoryRepository categoryRepository;

    @Test
    @DisplayName("Deve persistir um produto real no banco de dados e recuperá-lo")
    void integration_CreateAndFindProduct() {
        Company company = new Company();
        company.setBussinessName("Empresa Teste");
        company.setCnpj("12345678000199");
        company.setEmail("teste@empresa.com");
        company.setUsername("usuario.teste");
        company.setPassword("123456");
        company.setExpoPushToken("123456");
        company.setPhone("84999343899");

        companyRepository.save(company);

        Category category = new Category();
        category.setName("Eletrônicos");
        category = categoryRepository.save(category);

        ProductRequest request = new ProductRequest();
        request.setName("Mouse Gamer");
        request.setPrice(new BigDecimal("150.00"));
        request.setStock(10);
        request.setCompanyId(company.getUserId());
        request.setCategoryId(category.getId());

        ProductResponse saved = productService.create(request);

        Optional<Product> found = productRepository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("Mouse Gamer", found.get().getName());
    }
}

