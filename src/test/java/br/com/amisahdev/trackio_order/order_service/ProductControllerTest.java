package br.com.amisahdev.trackio_order.order_service;

import br.com.amisahdev.trackio_order.order_service.product.model.Product;
import br.com.amisahdev.trackio_order.order_service.user.models.Company;
import br.com.amisahdev.trackio_order.order_service.user.repository.CompanyRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("testing")
@AutoConfigureMockMvc
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CompanyRepository companyRepository;

    private String bearerToken;

    @BeforeEach
    void setup() {
        bearerToken = "Bearer mock-valid-jwt-token";

        companyRepository.deleteAll();
        Company company = new Company();
        company.setBussiness_name("Test Company");
        company.setCnpj("999999999");
        company.setEmail("track@email.com");
        company.setPhone("123456789");
        company.setUsername("track");
        company.setPassword("track");
        company.setExpoPushToken("mock-valid-expo-push-token");
        companyRepository.save(company);
    }


    @Test
    @DisplayName("Criar um produto com informações válidas")
    void shouldCreateProductSuccessfully() throws Exception {
        Product product = new Product();
        product.setName("Notebook Dell");
        product.setDescription("Laptop with 16GB RAM");
        product.setPrice(BigDecimal.valueOf(4500.00));
        product.setStock(10);
        product.setImage_url("https://example.com/image.jpg");
        product.setCompany(companyRepository.findAll().getFirst());


        mockMvc.perform(post("/api/product")
                        .header("Authorization", bearerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("The product was created"))
                .andExpect(jsonPath("$.status").value(201));
    }

    @Test
    @DisplayName("Verificar a validação dos campos obrigatórios - `stock` ausente")
    void shouldReturnErrorWhenStockIsMissing() throws Exception {
        String invalidJson = """
            {
              "name": "Mouse Logitech",
              "description": "Full HD",
              "price": 120.00
            }
            """;

        mockMvc.perform(post("/api/product")
                        .header("Authorization", bearerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("`stock` is required"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("Verificar a validação de tipo dos campos - `price` como string")
    void shouldReturnErrorWhenPriceIsString() throws Exception {
        String invalidJson = """
            {
              "name": "Monitor LG",
              "description": "Full HD",
              "price": "notANumber",
              "stock": 5
            }
            """;

        mockMvc.perform(post("/api/product")
                        .header("Authorization", bearerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("`price` should be a double"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("Verificar a validação de tipo dos campos - `stock` como string")
    void shouldReturnErrorWhenStockIsString() throws Exception {
        String invalidJson = """
            {
              "name": "Teclado Mecânico",
              "description": "Switch Blue",
              "price": 250.00,
              "stock": "dez"
            }
            """;

        mockMvc.perform(post("/api/product")
                        .header("Authorization", bearerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("`stock` should be a int"))
                .andExpect(jsonPath("$.status").value(400));
    }
}
