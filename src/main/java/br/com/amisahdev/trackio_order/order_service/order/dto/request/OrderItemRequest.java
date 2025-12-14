package br.com.amisahdev.trackio_order.order_service.order.dto.request;



import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class OrderItemRequest {

    @NotNull(message = "orderId is required")
    private Long orderId;

    @NotNull(message = "productId is required")
    private Long productId;

    @NotNull(message = "quantity is required")
    @Positive(message = "Quantity must be greater than zero")
    private Double quantity;

    @NotNull(message = "Price is required")
    @Positive(message = "price must be greater than zero")
    private BigDecimal price;
}
