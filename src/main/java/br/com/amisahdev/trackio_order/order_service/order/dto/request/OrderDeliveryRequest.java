package br.com.amisahdev.trackio_order.order_service.order.dto.request;

import br.com.amisahdev.trackio_order.order_service.user.models.DeliveryPerson;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderDeliveryRequest {
    @NotNull(message = "deliveryId is required")
    private Long deliveryId;
    @NotNull(message = "orderId is required")
    private Long orderId;
}
