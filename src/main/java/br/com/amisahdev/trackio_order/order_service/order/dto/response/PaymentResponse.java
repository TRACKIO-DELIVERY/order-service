package br.com.amisahdev.trackio_order.order_service.order.dto.response;


import br.com.amisahdev.trackio_order.order_service.order.model.Order;
import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class PaymentResponse {
    private Long id;
    private Long orderId;
    private String paymentMethod;
    private BigDecimal amount;
    private LocalDateTime paymentDate;
}
