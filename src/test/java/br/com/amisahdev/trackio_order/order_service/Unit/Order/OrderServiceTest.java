package br.com.amisahdev.trackio_order.order_service.Unit.Order;

import br.com.amisahdev.trackio_order.order_service.order.Repository.OrderRepository;
import br.com.amisahdev.trackio_order.order_service.order.dto.request.*;
import br.com.amisahdev.trackio_order.order_service.order.dto.response.OrderResponse;
import br.com.amisahdev.trackio_order.order_service.order.mapper.OrderMapper;
import br.com.amisahdev.trackio_order.order_service.order.model.Order;
import br.com.amisahdev.trackio_order.order_service.order.model.OrderStatus;
import br.com.amisahdev.trackio_order.order_service.order.service.imp.OrderServiceImp;
import br.com.amisahdev.trackio_order.order_service.product.model.Product;
import br.com.amisahdev.trackio_order.order_service.product.repository.ProductRepository;
import br.com.amisahdev.trackio_order.order_service.user.models.Company;
import br.com.amisahdev.trackio_order.order_service.user.models.Customer;
import br.com.amisahdev.trackio_order.order_service.user.models.DeliveryPerson;
import br.com.amisahdev.trackio_order.order_service.user.repository.CompanyRepository;
import br.com.amisahdev.trackio_order.order_service.user.repository.CustomerRepository;
import br.com.amisahdev.trackio_order.order_service.user.repository.DeliveryPersonRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock private ProductRepository productRepository;
    @Mock private OrderMapper orderMapper;
    @Mock private OrderRepository orderRepository;
    @Mock private CustomerRepository customerRepository;
    @Mock private CompanyRepository companyRepository;
    @Mock private DeliveryPersonRepository deliveryPersonRepository;

    @InjectMocks private OrderServiceImp orderService;

    private Company createBaseCompany() {
        Company c = new Company();
        c.setDeliveryFee(BigDecimal.ZERO);
        return c;
    }

    private Product createBaseProduct() {
        Product p = new Product();
        p.setPrice(BigDecimal.ZERO);
        return p;
    }

    private OrderRequest createBaseRequest() {
        OrderRequest r = new OrderRequest();
        r.setCustomerId(1L);
        r.setCompanyId(1L);
        OrderItemRequest i = new OrderItemRequest();
        i.setProductId(10L);
        i.setQuantity(2.0);
        r.setItems(List.of(i));
        return r;
    }

    @Test
    @DisplayName("1. Módulo Order - Unitário: Criação com sucesso")
    void create_Success() {
        when(customerRepository.findById(any())).thenReturn(Optional.of(new Customer()));
        when(companyRepository.findById(any())).thenReturn(Optional.of(createBaseCompany()));
        when(productRepository.findById(any())).thenReturn(Optional.of(createBaseProduct()));
        when(orderMapper.toEntity(any())).thenReturn(new Order());
        when(orderRepository.save(any())).thenReturn(new Order());
        when(orderMapper.toResponse(any())).thenReturn(new OrderResponse());

        assertNotNull(orderService.create(createBaseRequest()));
    }

    @Test
    @DisplayName("2. Módulo Order - Unitário: Cálculo de Total")
    void create_CalculateTotal() {
        Product p = new Product();
        p.setPrice(new BigDecimal("50.00"));

        when(customerRepository.findById(any())).thenReturn(Optional.of(new Customer()));
        when(companyRepository.findById(any())).thenReturn(Optional.of(createBaseCompany()));
        when(productRepository.findById(any())).thenReturn(Optional.of(p));

        Order orderEntity = new Order();
        orderEntity.setItems(new ArrayList<>());
        when(orderMapper.toEntity(any())).thenReturn(orderEntity);
        when(orderRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        orderService.create(createBaseRequest());
        verify(orderRepository).save(argThat(o -> o.getOrderAmount().compareTo(new BigDecimal("100.00")) == 0));
    }

    @Test
    @DisplayName("3. Módulo Order - Unitário: Taxa de Entrega")
    void create_DeliveryFee() {
        Company company = new Company();
        company.setDeliveryFee(new BigDecimal("15.00"));

        when(customerRepository.findById(any())).thenReturn(Optional.of(new Customer()));
        when(companyRepository.findById(any())).thenReturn(Optional.of(company));
        when(productRepository.findById(any())).thenReturn(Optional.of(createBaseProduct()));

        Order orderEntity = new Order();
        orderEntity.setItems(new ArrayList<>());
        when(orderMapper.toEntity(any())).thenReturn(orderEntity);
        when(orderRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        orderService.create(createBaseRequest());
        verify(orderRepository).save(argThat(o -> o.getOrderFlee().compareTo(new BigDecimal("15.00")) == 0));
    }

    @Test
    @DisplayName("4. Módulo Order - Unitário: Vínculo de Entregador")
    void orderDelivery_Success() {
        Order order = new Order();
        order.setOrderStatus(OrderStatus.IN_PROGRESS);
        OrderDeliveryRequest req = new OrderDeliveryRequest();
        req.setOrderId(1L); req.setDeliveryId(2L);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(deliveryPersonRepository.findById(2L)).thenReturn(Optional.of(new DeliveryPerson()));
        when(orderRepository.save(any())).thenReturn(order);

        orderService.orderDelivery(req);
        assertEquals(OrderStatus.IN_ROUTE, order.getOrderStatus());
    }

    @Test
    @DisplayName("5. Módulo Order - Unitário: Bloqueio de Entrega")
    void orderDelivery_Fail() {
        Order order = new Order();
        order.setOrderStatus(OrderStatus.COMPLETED);
        OrderDeliveryRequest req = new OrderDeliveryRequest();
        req.setOrderId(1L); req.setDeliveryId(2L);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(deliveryPersonRepository.findById(2L)).thenReturn(Optional.of(new DeliveryPerson()));

        assertThrows(RuntimeException.class, () -> orderService.orderDelivery(req));
    }

    @Test
    @DisplayName("6. Módulo Order - Unitário: Cancelamento Inválido")
    void orderCancel_Fail() {
        Order order = new Order();
        order.setOrderStatus(OrderStatus.COMPLETED);
        OrderCompletedCanceledRequest req = new OrderCompletedCanceledRequest();
        req.setOrderId(1L);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(RuntimeException.class, () -> orderService.orderCancel(req));
    }

}