package br.com.amisahdev.trackio_order.order_service.order.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderCompletedCanceledRequest {
    @NotNull(message = "orderId is required")
    private Long orderId;
}
