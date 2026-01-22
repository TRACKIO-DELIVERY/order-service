package br.com.amisahdev.trackio_order.order_service.order.dto.response;

import br.com.amisahdev.trackio_order.order_service.order.model.OrderStatus;
import br.com.amisahdev.trackio_order.order_service.user.models.Company;
import br.com.amisahdev.trackio_order.order_service.user.models.Customer;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class OrderResponse {
    private Long id;

    private Long companyId;
    private String companyName;

    private Long customerId;
    private String customerName;

    private Long  deliveryId;
    private String deliveryName;

    private LocalDateTime orderDate;
    private BigDecimal orderAmount;
    private BigDecimal orderFlee;
    private OrderStatus orderStatus;
    private List<OrderItemResponse> items;
}
