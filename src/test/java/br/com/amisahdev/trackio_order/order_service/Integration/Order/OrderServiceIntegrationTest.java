package br.com.amisahdev.trackio_order.order_service.Integration.Order;

import br.com.amisahdev.trackio_order.order_service.order.Repository.OrderRepository;
import br.com.amisahdev.trackio_order.order_service.order.dto.request.OrderItemRequest;
import br.com.amisahdev.trackio_order.order_service.order.dto.request.OrderRequest;
import br.com.amisahdev.trackio_order.order_service.order.dto.response.OrderResponse;
import br.com.amisahdev.trackio_order.order_service.order.model.OrderStatus;
import br.com.amisahdev.trackio_order.order_service.order.service.imp.OrderServiceImp;
import br.com.amisahdev.trackio_order.order_service.product.model.Product;
import br.com.amisahdev.trackio_order.order_service.product.repository.ProductRepository;
import br.com.amisahdev.trackio_order.order_service.user.models.Company;
import br.com.amisahdev.trackio_order.order_service.user.models.Customer;
import br.com.amisahdev.trackio_order.order_service.user.repository.CompanyRepository;
import br.com.amisahdev.trackio_order.order_service.user.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional // Limpa o banco após cada teste
public class OrderServiceIntegrationTest {

    @Autowired private OrderServiceImp orderService;
    @Autowired private OrderRepository orderRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private CompanyRepository companyRepository;
    @Autowired private CustomerRepository customerRepository;

    private Company savedCompany;
    private Customer savedCustomer;
    private Product savedProduct;

    @BeforeEach
    void setUp() {
        Company company = new Company();
        company.setUsername("admin");
        company.setBussinessName("Trackio Food");
        company.setCnpj("29142202000151");
        company.setEmail("contato@trackio.com");
        company.setDeliveryFee(new BigDecimal("12.50"));
        company.setExpoPushToken("123456");
        company.setPhone("84999343899");
        company.setPassword("secure123");
        savedCompany = companyRepository.save(company);

        Customer customer = new Customer();
        customer.setUsername("lucas_user");
        customer.setDateOfBirth(Date.valueOf("2003-02-27"));
        customer.setCpf("123456789");
        customer.setEmail("lucas@teste.com");
        customer.setExpoPushToken("123456");
        customer.setPhone("84999343899");
        customer.setPassword("lucas123");
        customer.setImageUrl("https:/");
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

        OrderItemRequest item = new OrderItemRequest();
        item.setProductId(savedProduct.getId());
        item.setQuantity(3.0); // 3 * 35.00 = 105.00
        request.setItems(List.of(item));

        OrderResponse response = orderService.create(request);

        assertNotNull(response.getId());

        var orderInDb = orderRepository.findById(response.getId()).orElseThrow();

        assertEquals(OrderStatus.IN_PROGRESS, orderInDb.getOrderStatus());
        assertEquals(0, new BigDecimal("105.00").compareTo(orderInDb.getOrderAmount()));
        assertEquals(0, new BigDecimal("12.50").compareTo(orderInDb.getOrderFlee()));

        assertEquals(1, orderInDb.getItems().size());
        assertEquals(0, new BigDecimal("35.00").compareTo(orderInDb.getItems().get(0).getPrice()));
    }
}