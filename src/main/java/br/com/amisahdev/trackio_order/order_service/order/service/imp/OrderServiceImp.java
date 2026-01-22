package br.com.amisahdev.trackio_order.order_service.order.service.imp;

import br.com.amisahdev.trackio_order.order_service.order.Repository.OrderRepository;
import br.com.amisahdev.trackio_order.order_service.order.dto.request.OrderCompletedCanceledRequest;
import br.com.amisahdev.trackio_order.order_service.order.dto.request.OrderDeliveryRequest;
import br.com.amisahdev.trackio_order.order_service.order.dto.request.OrderItemRequest;
import br.com.amisahdev.trackio_order.order_service.order.dto.request.OrderRequest;
import br.com.amisahdev.trackio_order.order_service.order.dto.response.OrderResponse;
import br.com.amisahdev.trackio_order.order_service.order.mapper.OrderMapper;
import br.com.amisahdev.trackio_order.order_service.order.model.Order;
import br.com.amisahdev.trackio_order.order_service.order.model.OrderItem;
import br.com.amisahdev.trackio_order.order_service.order.model.OrderStatus;
import br.com.amisahdev.trackio_order.order_service.order.service.interf.OrderService;
import br.com.amisahdev.trackio_order.order_service.product.model.Product;
import br.com.amisahdev.trackio_order.order_service.product.repository.ProductRepository;
import br.com.amisahdev.trackio_order.order_service.user.models.Company;
import br.com.amisahdev.trackio_order.order_service.user.models.Customer;
import br.com.amisahdev.trackio_order.order_service.user.models.DeliveryPerson;
import br.com.amisahdev.trackio_order.order_service.user.repository.CompanyRepository;
import br.com.amisahdev.trackio_order.order_service.user.repository.CustomerRepository;
import br.com.amisahdev.trackio_order.order_service.user.repository.DeliveryPersonRepository;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Or;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImp implements OrderService {
    private final ProductRepository productRepository;
    private final OrderMapper orderMapper;
    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final CompanyRepository companyRepository;
    private final DeliveryPersonRepository deliveryPersonRepository;

    @Override
    @Transactional
    public OrderResponse create(OrderRequest request) {
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        Company company = companyRepository.findById(request.getCompanyId())
                .orElseThrow(() -> new RuntimeException("Company not found"));

        Order order = orderMapper.toEntity(request);
        order.setCustomer(customer);
        order.setCompany(company);
        order.setOrderDate(LocalDateTime.now());
        order.setOrderStatus(OrderStatus.IN_PROGRESS);

        if (company.getDeliveryFee().compareTo(BigDecimal.ZERO) > 0){
            order.setOrderFlee(company.getDeliveryFee());
        } else
            order.setOrderFlee(BigDecimal.ZERO);

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderItemRequest itemReq : request.getItems()) {
            Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found ID: " + itemReq.getProductId()));

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProduct(product);
            item.setQuantity(itemReq.getQuantity());
            item.setPrice(product.getPrice()); // Snapshot do preço atual

            orderItems.add(item);

            // Cálculo: totalAmount += price * quantity
            BigDecimal itemTotal = product.getPrice().multiply(BigDecimal.valueOf(itemReq.getQuantity()));
            totalAmount = totalAmount.add(itemTotal);
        }

        order.setItems(orderItems);
        order.setOrderAmount(totalAmount);

        return orderMapper.toResponse(orderRepository.save(order));
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse findById(Long id) {
        return orderRepository.findById(id)
                .map(orderMapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    @Override
    public List<OrderResponse> findAll() {
        return null;
    }

    @Override
    public void deleteById(Long id) {
    }

    @Override
    public OrderResponse update(OrderRequest request) {
        return null;
    }

    @Override
    @Transactional
    public OrderResponse orderDelivery(OrderDeliveryRequest request) {
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        DeliveryPerson deliveryPerson = deliveryPersonRepository.findById(request.getDeliveryId())
                .orElseThrow(() -> new RuntimeException("DeliveryPerson not found"));

        if (order.getOrderStatus().equals(OrderStatus.COMPLETED)) {
            throw new RuntimeException("Order status is COMPLETED");
        } else if (order.getOrderStatus().equals(OrderStatus.CANCELLED)) {
            throw new RuntimeException("Order status is CANCELLED");
        }

        order.setOrderStatus(OrderStatus.IN_ROUTE);
        order.setDeliveryPerson(deliveryPerson);

        return orderMapper.toResponse(orderRepository.save(order));
    }

    @Override
    @Transactional
    public OrderResponse orderCancel(OrderCompletedCanceledRequest request) {
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getOrderStatus().equals(OrderStatus.COMPLETED)){
            throw new RuntimeException("Order status is already completed");
        }

        order.setOrderStatus(OrderStatus.CANCELLED);

        return orderMapper.toResponse(orderRepository.save(order));
    }

    @Override
    @Transactional
    public OrderResponse orderCompleted(OrderCompletedCanceledRequest request) {
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getOrderStatus().equals(OrderStatus.CANCELLED)){
            throw new RuntimeException("Order status is already cancelled");
        }

        order.setOrderStatus(OrderStatus.COMPLETED);

        return orderMapper.toResponse(orderRepository.save(order));
    }
}
