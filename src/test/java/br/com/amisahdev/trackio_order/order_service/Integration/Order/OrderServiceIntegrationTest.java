package br.com.amisahdev.trackio_order.order_service.Integration.Order;

import br.com.amisahdev.trackio_order.order_service.order.dto.request.PaymentRequest;
import br.com.amisahdev.trackio_order.order_service.order.repository.OrderRepository;
import br.com.amisahdev.trackio_order.order_service.order.dto.request.OrderItemRequest;
import br.com.amisahdev.trackio_order.order_service.order.dto.request.OrderRequest;
import br.com.amisahdev.trackio_order.order_service.order.dto.response.OrderResponse;
import br.com.amisahdev.trackio_order.order_service.order.model.OrderStatus;
import br.com.amisahdev.trackio_order.order_service.order.service.imp.OrderServiceImp;
import br.com.amisahdev.trackio_order.order_service.product.model.Product;
import br.com.amisahdev.trackio_order.order_service.product.repository.ProductRepository;
import br.com.amisahdev.trackio_order.order_service.services.AmazonS3Service;
import br.com.amisahdev.trackio_order.order_service.user.models.Company;
import br.com.amisahdev.trackio_order.order_service.user.models.Customer;
import br.com.amisahdev.trackio_order.order_service.user.repository.CompanyRepository;
import br.com.amisahdev.trackio_order.order_service.user.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@TestPropertySource(properties = {
        "BUCKET_NAME=teste-bucket",
        "aws.region=us-east-1",
        "aws.accessKey=fakeKey",
        "aws.secretKey=fakeSecret"
})
public class OrderServiceIntegrationTest {

    @Autowired private OrderServiceImp orderService;
    @Autowired private OrderRepository orderRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private CompanyRepository companyRepository;
    @Autowired private CustomerRepository customerRepository;
    @MockitoBean
    private AmazonS3Service amazonS3Service;

    private Company savedCompany;
    private Customer savedCustomer;
    private Product savedProduct;

    @BeforeEach
    void setUp() {
        Company company = Company.builder()
                .username("admin_company")
                .bussinessName("Trackio Food")
                .cnpj("29142202000151")
                .email("contato@trackio.com")
                .deliveryFee(new BigDecimal("12.50"))
                .keycloakUserId(UUID.randomUUID())
                .fullname("Trackio Food")
                .build();
        savedCompany = companyRepository.save(company);

        Customer customer = new Customer();
        customer.setUsername("lucas_user");
        customer.setEmail("lucas@teste.com");
        customer.setCpf("12345678901");
        customer.setDateOfBirth(new Date());
        customer.setImageUrl("https://image.com");
        customer.setKeycloakUserId(UUID.randomUUID());
        customer.setFullname("Trackio Food");
        savedCustomer = customerRepository.save(customer);

        Product product = new Product();
        product.setName("Hambúrguer Artesanal");
        product.setPrice(new BigDecimal("35.00"));
        product.setStock(50);
        savedProduct = productRepository.save(product);
    }

    @Test
    @DisplayName("Integração: Deve persistir pedido completo e validar cálculos no H2")
    void integration_CreateOrderFlow() {
        OrderRequest request = new OrderRequest();
        request.setCompanyId(savedCompany.getUserId());
        request.setCustomerId(savedCustomer.getUserId());

        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setPaymentMethod("CREDIT_CARD");
        request.setPayment(paymentRequest);

        OrderItemRequest item = new OrderItemRequest();
        item.setProductId(savedProduct.getId());
        item.setQuantity(3.0);
        request.setItems(List.of(item));

        OrderResponse response = orderService.create(request);

        assertNotNull(response.getId());

        var orderInDb = orderRepository.findById(response.getId()).orElseThrow();

        assertEquals(OrderStatus.IN_PROGRESS, orderInDb.getOrderStatus());
        assertTrue(new BigDecimal("105.00").compareTo(orderInDb.getOrderAmount()) == 0);
    }
}